package com.partyquest.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.partyquest.backend.config.WithAccount;
import com.partyquest.backend.domain.dto.PartyDto;
import com.partyquest.backend.domain.dto.QuestDto;
import com.partyquest.backend.domain.type.QuestType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
public class QuestControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    PartyDto.CreatePartyDto.Request makeParty(String title, String description) {
        return PartyDto.CreatePartyDto.Request.builder()
                .title(title)
                .description(description)
                .isPublic(true)
                .build();
    }
    int createParty(String title, String description) throws Exception {
        PartyDto.CreatePartyDto.Request request = makeParty(title
                ,description);
        MvcResult result = mockMvc.perform(
                post("/party")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(objectMapper.writeValueAsString(request))
        ).andReturn();
        List<HashMap<String,Object>> td = (List<HashMap<String, Object>>) objectMapper.readValue(result.getResponse().getContentAsString(),HashMap.class).get("data");
        return (int) td.get(0).get("id");
    }


    @Nested
    @DisplayName("퀘스트_생성")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class CreateQuestTestClass {

        private static int partyID;
        private static int parentsQuestID;

        @Test
        @Order(1)
        @DisplayName("사전작업01=파티_생성")
        @WithAccount("create_party_create_quest")
        void pre01() throws Exception{
            partyID = createParty("create_party_create_quest_title","create_party_create_quest_description");
        }

        @Test
        @Order(2)
        @DisplayName("메인테스트01=퀘스트_생성")
        @WithAccount("create_party_create_quest")
        void main01() throws Exception {
            QuestDto.CreateQuestDto.Request request =
                    QuestDto.CreateQuestDto.Request.builder()
                            .questID(null)
                            .partyID((long) partyID)
                            .startTime("2023-01-01")
                            .endTime("2023-01-31")
                            .type(QuestType.NOTIFICATION)
                            .title("create_quest_title")
                            .description("create_quest_description")
                            .build();

            MvcResult result = mockMvc.perform(
                    post("/quest")
                            .contentType("application/json")
                            .accept("application/json")
                            .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isCreated()).andDo(print()).andReturn();
            List<HashMap<String,Object>> td = (List<HashMap<String, Object>>) objectMapper.readValue(result.getResponse().getContentAsString(),HashMap.class).get("data");
            parentsQuestID = (int) td.get(0).get("id");
        }

        @Test
        @Order(3)
        @DisplayName("메인테스트02=퀘스트_제출")
        @WithAccount("create_party_create_quest")
        void main02() throws Exception {
            QuestDto.CreateQuestDto.Request request =
                QuestDto.CreateQuestDto.Request.builder()
                        .questID((long) parentsQuestID)
                        .partyID((long) partyID)
                        .startTime("2023-01-01")
                        .endTime("2023-01-31")
                        .type(QuestType.SUBMIT)
                        .title("create_quest_title")
                        .description("create_quest_description")
                        .build();

            MvcResult result = mockMvc.perform(
                    post("/quest")
                            .contentType("application/json")
                            .accept("application/json")
                            .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isCreated()).andDo(print()).andReturn();
        }
    }
}
