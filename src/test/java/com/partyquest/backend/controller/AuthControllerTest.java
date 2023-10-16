package com.partyquest.backend.controller;

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
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
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
                .birth("birth")
                .nickname("nickname")
                .password("password")
                .build();

        mockMvc.perform(RestDocumentationRequestBuilders
                .post("/auth/signup")
                .contentType("application/json")
                .accept("application/json")
                .content(objectMapper.writeValueAsString(dto))
        ).andDo(document("auth")).andExpect(status().isCreated());
    }

//    @Test
//    @DisplayName("LOCAL LOGIN TEST")
//    void log_in() throws Exception {
//        AuthDto.SignupDto dto = AuthDto.SignupDto.builder()
//                .email("email")
//                .birth("birth")
//                .nickname("nickname")
//                .password("password")
//                .build();
//
//        mockMvc.perform(RestDocumentationRequestBuilders
//                .post("/auth/signup")
//                .contentType("application/json")
//                .accept("application/json")
//                .content(objectMapper.writeValueAsString(dto))
//        );
//
//
//    }
}