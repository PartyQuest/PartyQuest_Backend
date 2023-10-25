package com.partyquest.backend.service.impl.oauth2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.partyquest.backend.config.exception.ErrorCode;
import com.partyquest.backend.config.exception.OAuth2Exception;
import com.partyquest.backend.service.impl.oauth2.profile.GoogleProfile;
import com.partyquest.backend.service.impl.oauth2.profile.KakaoProfile;
import com.partyquest.backend.service.impl.oauth2.profile.NaverProfile;
import com.partyquest.backend.service.impl.oauth2.profile.ProfileDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@Service
@Slf4j
public class ProviderService {

    private final OAuthRequestFactory oAuthRequestFactory;

    @Autowired
    public ProviderService(OAuthRequestFactory oAuthRequestFactory) {
        this.oAuthRequestFactory = oAuthRequestFactory;
    }

    public AccessToken getAccessToken(String code, String provider) {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        OAuthRequest oAuthRequest = oAuthRequestFactory.getRequest(code,provider);
        HttpEntity<LinkedMultiValueMap<String,String>> request = new HttpEntity<>(oAuthRequest.getMap(), httpHeaders);

        ResponseEntity<String> response = restTemplate.postForEntity(oAuthRequest.getUrl(), request, String.class);
        try {
            if (response.getStatusCode() == HttpStatus.OK) return objectMapper.readValue(response.getBody(),AccessToken.class);
        } catch (Exception e) {
            throw new OAuth2Exception(e.getMessage(), ErrorCode.OAUTH2_ERROR);
        }
        throw new OAuth2Exception("other oauth2 error", ErrorCode.OAUTH2_ERROR);
    }

    public ProfileDto getProfile(String accessToken, String provider) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set("Authorization", "Bearer " + accessToken);

        String profileUrl = oAuthRequestFactory.getProfileUrl(provider);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<String> response = restTemplate.postForEntity(profileUrl, request, String.class);

        try {
            if (response.getStatusCode() == HttpStatus.OK) {
                return extractProfile(response, provider);
            }
        } catch (Exception e) {
            throw new OAuth2Exception(e.getMessage(), ErrorCode.OAUTH2_ERROR);
        }
        throw new OAuth2Exception("other oauth2 error", ErrorCode.OAUTH2_ERROR);
    }

    private ProfileDto extractProfile(ResponseEntity<String> response, String provider) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        if (provider.equals("kakao")) {
            KakaoProfile kakaoProfile = objectMapper.readValue(response.getBody(), KakaoProfile.class);
            return new ProfileDto(kakaoProfile.getKakao_account().getEmail(), kakaoProfile.getKakao_account().getProfile().getNickname(), provider);
//            return new ProfileDto(kakaoProfile.getKakao_account().getEmail());
        } else if(provider.equals("google")) {
            GoogleProfile googleProfile = objectMapper.readValue(response.getBody(), GoogleProfile.class);
            return new ProfileDto(googleProfile.getEmail(),null,provider);
        } else {
            NaverProfile naverProfile = objectMapper.readValue(response.getBody(), NaverProfile.class);
            return new ProfileDto(naverProfile.getResponse().getEmail(),null,provider);
        }
    }
}
