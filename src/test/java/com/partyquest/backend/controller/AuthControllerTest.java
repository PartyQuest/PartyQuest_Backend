package com.partyquest.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.partyquest.backend.domain.dto.AuthDto;
import com.partyquest.backend.service.logic.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AuthService authService;

    @Test
    @DisplayName("REGISTER USER TEST")
    void sign_up() throws Exception {
        AuthDto.SignupDto dto = AuthDto.SignupDto.builder()
                .email("email")
                .nickname("nickname")
                .password("password")
                .build();

        mockMvc.perform(RestDocumentationRequestBuilders
                .post("/auth/signup")
                .contentType("application/json")
                .accept("application/json")
                .content(objectMapper.writeValueAsString(dto))
        ).andDo(
                document(
                        "signup",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("User email"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("User nickname"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("Encrypt password")
                        )
                        )).andExpect(status().isCreated());
    }

    @Test
    @DisplayName("LOCAL LOGIN TEST")
    void log_in() throws Exception {
        AuthDto.SignupDto dto = AuthDto.SignupDto.builder()
                .email("email2")
                .nickname("nickname")
                .password("password")
                .build();

        mockMvc.perform(RestDocumentationRequestBuilders
                .post("/auth/signup")
                .contentType("application/json")
                .accept("application/json")
                .content(objectMapper.writeValueAsString(dto))
        );

        AuthDto.LoginRequestDto loginDto = AuthDto.LoginRequestDto.builder()
                .email("email2")
                .password("password")
                .build();

        mockMvc.perform(RestDocumentationRequestBuilders
                .post("/auth/login")
                .contentType("application/json")
                .accept("application/json")
                .content(objectMapper.writeValueAsString(loginDto))
        ).andDo(document(
                "login",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING).description("User email"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("Encrypt password")
                )))
                .andDo(print()).andExpect(status().isOk());

    }
    @Test
    @DisplayName("OAUTH_LOGIN")
    void OAuthLoginTest() throws Exception {
        AuthDto.OAuthLogin.Request request = AuthDto.OAuthLogin.Request.builder()
                .email("testEmail")
                .nickname("nickname")
                .secrets("$2a$10$ijPd8VVjA1r2UdmyOntpdOSyO4mUHd1xeWfuB5aGzWe3jUrYVuYB2")
                .build();

        mockMvc.perform(RestDocumentationRequestBuilders
                .post("/auth/login/oauth/{provider}","oauthProvider")
                .contentType("application/json")
                .accept("application/json")
                .content(objectMapper.writeValueAsString(request))
        ).andDo(
                document(
                        "OAuthLogin",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("OAuth User Email"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("OAuth User nickname"),
                                fieldWithPath("secrets").type(JsonFieldType.STRING).description("OAuth Client-Server connect secret key")
                        ),
                        pathParameters(parameterWithName("provider").description("OAuth Provider"))
                )
        ).andDo(print()).andExpect(status().isOk());
    }
}