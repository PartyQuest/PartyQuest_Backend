package com.partyquest.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.partyquest.backend.config.WithAccount;
import com.partyquest.backend.domain.dto.PartyDto;
import com.partyquest.backend.domain.entity.User;
import com.partyquest.backend.domain.repository.FileRepository;
import com.partyquest.backend.domain.repository.PartyRepository;
import com.partyquest.backend.domain.repository.UserPartyRepository;
import com.partyquest.backend.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import static com.partyquest.backend.domain.dto.PartyDto.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class PartyControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PartyRepository partyRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserPartyRepository userPartyRepository;
    @Autowired
    FileRepository fileRepository;

    @Test
    @DisplayName("CREATE_PARTY")
    @WithAccount("email")
    void create_party() throws Exception {
        CreatePartyDto.Request clientRequest = CreatePartyDto.Request.builder()
                .title("title")
                .isPublic(true)
                .description("description")
                .build();

        mockMvc.perform(RestDocumentationRequestBuilders
                .post("/party")
                .contentType("application/json")
                .accept("application/json")
                .content(objectMapper.writeValueAsString(clientRequest))
        ).andDo(
                document(
                        "create_party",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("Party title"),
                                fieldWithPath("isPublic").type(JsonFieldType.BOOLEAN).description("Party view Public/Private"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("Party description")
                        )
                )
        ).andDo(print()).andExpect(status().isCreated());
    }

    @Test
    @DisplayName("READ_PARTY_LIST")
    @WithAccount("email")
    void read_party_list() throws Exception {

        CreatePartyDto.Request clientRequest = CreatePartyDto.Request.builder()
                .title("title")
                .isPublic(true)
                .description("description")
                .build();

        mockMvc.perform(RestDocumentationRequestBuilders
                .post("/party")
                .contentType("application/json")
                .accept("application/json")
                .content(objectMapper.writeValueAsString(clientRequest))
        ).andDo(
                document(
                        "create_party",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                )
        ).andDo(print()).andExpect(status().isCreated());

        mockMvc.perform(RestDocumentationRequestBuilders
                .get("/party?id=1")
        ).andDo(print()).andExpect(status().isOk());

        mockMvc.perform(RestDocumentationRequestBuilders
                .get("/party?master=nickname&title=title&id=1")
        ).andDo(
                document(
                        "read_party_list",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("master").description("Party master name"),
                                parameterWithName("title").description("Party title"),
                                parameterWithName("id").description("Party id")
                        )
                )
        ).andDo(print()).andExpect(status().isOk());
    }

    @WithAccount("email1")
    void createParty() throws Exception {
        CreatePartyDto.Request clientRequest = CreatePartyDto.Request.builder()
                .title("title")
                .isPublic(true)
                .description("description")
                .build();

        mockMvc.perform(RestDocumentationRequestBuilders
                .post("/party")
                .contentType("application/json")
                .accept("application/json")
                .content(objectMapper.writeValueAsString(clientRequest))
        ).andDo(print()).andExpect(status().isCreated());
    }

    @Test
    @DisplayName("APPLICATION_PARTY")
    @WithAccount("email2")
    void applicationParty() throws Exception {
        createParty();
        User user = User.builder()
                .deviceTokens(null)
                .password("password")
                .email("email")
                .nickname("nickname")
                .build();
        User save = userRepository.save(user);

        PartyDto.ApplicationPartyDto.Request request = ApplicationPartyDto.Request.builder()
                .partyName("title")
                .partId(1L)
                .userId(save.getId())
                .build();

        mockMvc.perform(RestDocumentationRequestBuilders
                .post("/party/application")
                .contentType("application/json")
                .accept("application/json")
                .content(objectMapper.writeValueAsString(request))
        ).andDo(
                document(
                        "application_party",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("partyName").type(JsonFieldType.STRING).description("Party Name"),
                                fieldWithPath("partId").type(JsonFieldType.NUMBER).description("Party ID"),
                                fieldWithPath("userId").type(JsonFieldType.NUMBER).description("User ID")
                        )
                )
                )
                .andDo(print());
    }

    @Test
    @DisplayName("APPLICATIOR LIST")
    @WithAccount("email3")
    void applicator() throws Exception {
        createParty();
        User user = User.builder()
                .deviceTokens(null)
                .password("password")
                .email("email4")
                .nickname("nickname")
                .build();
        User save = userRepository.save(user);
        PartyDto.ApplicationPartyDto.Request request = ApplicationPartyDto.Request.builder()
                .partyName("title")
                .partId(1L)
                .userId(save.getId())
                .build();

        mockMvc.perform(RestDocumentationRequestBuilders
                        .post("/party/application")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(objectMapper.writeValueAsString(request))
                ).andDo(print());

        mockMvc.perform(RestDocumentationRequestBuilders
                .get("/party/member?grade=NO_MEMBER&partyID=1")
                .contentType("application/json")
                .accept("application/json")
        ).andDo(print());
    }
}