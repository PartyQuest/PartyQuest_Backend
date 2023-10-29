package com.partyquest.backend.service.impl;

import com.partyquest.backend.domain.dto.AuthDto;
import com.partyquest.backend.domain.dto.PartyDto;
import com.partyquest.backend.domain.entity.Party;
import com.partyquest.backend.domain.repository.PartyRepository;
import com.partyquest.backend.domain.repository.UserPartyRepository;
import com.partyquest.backend.service.logic.AuthService;
import com.partyquest.backend.service.logic.PartyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PartyServiceImplTest {

    @Autowired
    AuthService authService;

    @Autowired
    PartyService partyService;

    @Autowired
    PartyRepository partyRepository;

    @Autowired
    UserPartyRepository userPartyRepository;

    @Test
    @DisplayName("CREATE PARTY")
    void CreateParty() {
        AuthDto.SignupResponseDto dto = authService.SignUp(
                AuthDto.SignupDto.builder()
                        .password("password")
                        .nickname("nickname")
                        .birth("birth")
                        .email("email1")
                        .build(),
                "LOCAL");

        long id = authService.getUserByEmail("email1").getId();

        partyService.createPartyDto(PartyDto.CreatePartyDto.Request.builder()
                        .description("description")
                        .isPublic(true)
                        .title("title1")
                .build(),id);

        Party result = partyRepository.findByTitle("title1").get(0);

        System.out.println(userPartyRepository.findByParty(result).getMemberGrade());

        assertAll(
                () -> assertEquals(result.getTitle(), "title1"),
                () -> assertEquals(result.getCapabilities(), 20),
                () -> assertEquals(result.getDescription(), "description")
        );
    }
}