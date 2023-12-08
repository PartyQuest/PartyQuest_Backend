package com.partyquest.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.partyquest.backend.config.WithAccount;
import com.partyquest.backend.domain.entity.UserParty;
import com.partyquest.backend.domain.repository.FileRepository;
import com.partyquest.backend.domain.repository.PartyRepository;
import com.partyquest.backend.domain.repository.UserPartyRepository;
import com.partyquest.backend.domain.repository.UserRepository;
import com.partyquest.backend.domain.type.PartyMemberType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.*;

import static com.partyquest.backend.domain.dto.PartyDto.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertAll;

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

    CreatePartyDto.Request makeParty(String title, String description) {
        return CreatePartyDto.Request.builder()
                .title(title)
                .description(description)
                .isPublic(true)
                .build();
    }

    @Nested
    @DisplayName("파티를_생성한다")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class CreatePartyTestClass {
        @Test
        @Order(1)
        @DisplayName("메인테스트01=파티_생성")
        @WithAccount("create_tester01")
        void createParty() throws Exception {
            mockMvc.perform(RestDocumentationRequestBuilders
                    .post("/party")
                    .contentType("application/json")
                    .accept("application/json")
                    .content(objectMapper.writeValueAsString(
                            makeParty("create party test title",
                                    "create party test description")))
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
            ).andExpect(status().isCreated());
        }

        @Test
        @Order(999)
        @DisplayName("데이터베이스 초기화")
        void init() {
            fileRepository.deleteAll();
            userPartyRepository.deleteAll();
            partyRepository.deleteAll();
            userRepository.deleteAll();
        }
    }
    @Nested
    @DisplayName("파티_목록을_조회한다")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ReadPartyTestClass {
        @Test
        @Order(1)
        @DisplayName("사전작업01=파티_5회_생성")
        @WithAccount("preprocess_make_party_tester01")
        void preprocess_party() throws Exception {
            for(int i = 1; i <= 5; i++) {
                CreatePartyDto.Request request = makeParty("preprocess_party_title"+i,
                        "preprocess_party_description");
                mockMvc.perform(
                        post("/party")
                                .contentType("application/json")
                                .accept("application/json")
                                .content(objectMapper.writeValueAsString(request))
                ).andReturn();
            }
        }

        @Test
        @Order(2)
        @DisplayName("메인테스트01=조건_없이_전부_탐색")
        @WithAccount("read_success_tester01")
        void read_success_test() throws Exception {
            MvcResult result = mockMvc.perform(
                    get("/party")
            ).andExpect(status().isOk()).andReturn();

            HashMap<String,Object> map = objectMapper.readValue(result.getResponse().getContentAsString(), HashMap.class);
            List<List<HashMap<String,Object>>> inner = (List<List<HashMap<String, Object>>>) map.get("data");

            assertAll(
                    () -> assertEquals(inner.get(0).size(), 5)
            );
        }

        @Test
        @Order(2)
        @DisplayName("메인테스트02=일부_조건_탐색")
        @WithAccount("read_success_tester02")
        void read_success_condition_test() throws Exception{
            MvcResult result = mockMvc.perform(
                    get("/party").param("title", "preprocess_party_title1")
            ).andExpect(status().isOk()).andReturn();

            HashMap<String,Object> map = objectMapper.readValue(result.getResponse().getContentAsString(), HashMap.class);
            List<List<HashMap<String,Object>>> inner = (List<List<HashMap<String, Object>>>) map.get("data");

            assertAll(
                    ()->assertEquals(inner.get(0).size(),1)
            );
        }

        @Test
        @Order(2)
        @DisplayName("메인테스트03=모든_조건_탐색")
        @WithAccount("read_success_tester03")
        void read_success_all_condition_test() throws Exception {
            MvcResult result = mockMvc.perform(get("/party")
                    .param("master", "preprocess_make_party_tester01")
                    .param("title", "preprocess_party_title1")
            ).andDo(
                    document(
                            "read_party_list",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            queryParameters(
                                    parameterWithName("master").description("Party master name"),
                                    parameterWithName("title").description("Party title")
                            )
                    )
            ).andDo(print()).andExpect(status().isOk()).andReturn();

            HashMap<String,Object> map = objectMapper.readValue(result.getResponse().getContentAsString(), HashMap.class);
            List<List<HashMap<String,Object>>> inner = (List<List<HashMap<String, Object>>>) map.get("data");

            assertAll(
                    ()->assertEquals(inner.get(0).size(),1)
            );
        }
        @Test
        @Order(999)
        @DisplayName("데이터베이스 초기화")
        void init() {
            fileRepository.deleteAll();
            userPartyRepository.deleteAll();
            partyRepository.deleteAll();
            userRepository.deleteAll();
        }
    }
    @Nested
    @DisplayName("파티_가입을_신청한다")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ApplicationPartyTestClass {
        public static int partyID;
        @Test
        @Order(1)
        @DisplayName("사전작업01=파티_생성")
        @WithAccount("preprocess_make_party_application_tester01")
        void preprocess_party() throws Exception {
            CreatePartyDto.Request request = makeParty("preprocess_party_title",
                    "preprocess_party_description");
            MvcResult result = mockMvc.perform(
                    post("/party")
                            .contentType("application/json")
                            .accept("application/json")
                            .content(objectMapper.writeValueAsString(request))
            ).andReturn();
            List<HashMap<String,Object>> td = (List<HashMap<String, Object>>) objectMapper.readValue(result.getResponse().getContentAsString(),HashMap.class).get("data");
            partyID = (int) td.get(0).get("id");
        }

        @Test
        @Order(2)
        @DisplayName("메인테스트01=파티_신청")
        @WithAccount("application_success_tester01")
        void application_success_test() throws Exception {
            ApplicationPartyDto.Request request = ApplicationPartyDto.Request.builder()
                    .partyId(partyID)
                    .partyName("preprocess_party_title")
                    .build();

            MvcResult result = mockMvc.perform(RestDocumentationRequestBuilders
                    .post("/party/application")
                    .contentType("application/json")
                    .accept("application/json")
                    .content(objectMapper.writeValueAsString(request))
            ).andDo(
                    document(
                            "party_application",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestFields(
                                    fieldWithPath("partyId").type(JsonFieldType.NUMBER).description("application party ID"),
                                    fieldWithPath("partyName").type(JsonFieldType.STRING).description("application party title")
                            )
                    )
                    )
                    .andExpect(status().isOk()).andReturn();

            List<HashMap<String,Object>> data =
                    (List<HashMap<String, Object>>) objectMapper.readValue(result.getResponse().getContentAsString(),HashMap.class).get("data");

            assertAll(
                    () -> assertEquals(data.get(0).size(), 3)
            );
        }

        @Test
        @Order(2)
        @DisplayName("실패테스트02=파티_신청_파티찾기_불가")
        @WithAccount("application_failed_tester01")
        void application_failed_test() throws Exception{
            ApplicationPartyDto.Request request = ApplicationPartyDto.Request.builder()
                    .partyId(9999)
                    .partyName("preprocess_party_title")
                    .build();

            MvcResult result = mockMvc.perform(RestDocumentationRequestBuilders
                    .post("/party/application")
                    .contentType("application/json")
                    .accept("application/json")
                    .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isBadRequest()).andReturn();

        }

        @Test
        @Order(999)
        @DisplayName("데이터베이스 초기화")
        void init() {
            fileRepository.deleteAll();
            userPartyRepository.deleteAll();
            partyRepository.deleteAll();
            userRepository.deleteAll();
        }
    }
    @Nested
    @DisplayName("파티_멤버를_등급별로_조회한다")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class MemberGradeTestClass {
        public static int partyID;
        @Test
        @Order(1)
        @DisplayName("사전작업01=파티_생성")
        @WithAccount("preprocessing_make_party_search_grade_tester01")
        void preprocessing_make_party_search_grade_test() throws Exception{
            CreatePartyDto.Request request = makeParty("preprocess_party_title",
                    "preprocess_party_description");
            MvcResult result = mockMvc.perform(
                    post("/party")
                            .contentType("application/json")
                            .accept("application/json")
                            .content(objectMapper.writeValueAsString(request))
            ).andReturn();
            List<HashMap<String,Object>> td = (List<HashMap<String, Object>>) objectMapper.readValue(result.getResponse().getContentAsString(),HashMap.class).get("data");
            partyID = (int) td.get(0).get("id");
        }

        @Test
        @Order(2)
        @DisplayName("사전작업02=파티_신청")
        @WithAccount("preprocessing_application_party_tester01")
        void preprocessing_application_party_test01() throws Exception {
            ApplicationPartyDto.Request request = ApplicationPartyDto.Request.builder()
                    .partyId(partyID)
                    .partyName("preprocess_party_title")
                    .build();
            mockMvc.perform(
                    post("/party/application")
                            .contentType("application/json")
                            .accept("application/json")
                            .content(objectMapper.writeValueAsString(request))
            ).andDo(print());
        }

        @Test
        @Order(3)
        @DisplayName("메인테스트01=파티원_검색")
        @WithAccount("preprocessing_make_party_search_grade_tester01")
        void applicator_search_test() throws Exception {
            mockMvc.perform(get("/party/member?partyID="+partyID)).andDo(print()).andExpect(status().isOk());
        }

        @Test
        @Order(4)
        @DisplayName("실패테스트02=파티원_검색_실패")
        @WithAccount("preprocessing_make_party_search_grade_tester01")
        void applicator_search_test_failed() throws Exception {
            mockMvc.perform(get("/party/member?partyID=100")).andDo(print()).andExpect(status().isBadRequest());
        }
        @Test
        @Order(999)
        @DisplayName("데이터베이스 초기화")
        void init() {
            fileRepository.deleteAll();
            userPartyRepository.deleteAll();
            partyRepository.deleteAll();
            userRepository.deleteAll();
        }
    }
    @Nested
    @DisplayName("파티_신청자를_파티원으로_전환한다.")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class AcceptMemberTestClass {

        public static Long partyID;
        public static List<Long> userID = new ArrayList<>();

        @Test
        @Order(1)
        @DisplayName("사전작업01=파티_생성")
        @WithAccount("preprocessing_make_party_accept_member_tester01")
        void preprocessing_make_party_search_grade_test() throws Exception{
            CreatePartyDto.Request request = makeParty("preprocess_party_title",
                    "preprocess_party_description");
            MvcResult result = mockMvc.perform(
                    post("/party")
                            .contentType("application/json")
                            .accept("application/json")
                            .content(objectMapper.writeValueAsString(request))
            ).andReturn();
            List<HashMap<String,Object>> td = (List<HashMap<String, Object>>) objectMapper.readValue(result.getResponse().getContentAsString(),HashMap.class).get("data");
            partyID = (long)(int)td.get(0).get("id");
        }

        @Test
        @Order(2)
        @DisplayName("사전작업02=파티_신청")
        @WithAccount("preprocessing_application_party_tester01")
        void preprocessing_application_party_test() throws Exception {
            ApplicationPartyDto.Request request = ApplicationPartyDto.Request.builder()
                    .partyId(partyID)
                    .partyName("preprocess_party_title")
                    .build();
            MvcResult result = mockMvc.perform(
                    post("/party/application")
                            .contentType("application/json")
                            .accept("application/json")
                            .content(objectMapper.writeValueAsString(request))
            ).andDo(print()).andReturn();

            List<HashMap<String,Object>> data = (List<HashMap<String, Object>>) objectMapper.readValue(result.getResponse().getContentAsString(), HashMap.class).get("data");
            userID.add((long)(int)data.get(0).get("userId"));
        }

        @Test
        @Order(3)
        @DisplayName("메인테스트01=파티원_전환_성공")
        @WithAccount("preprocessing_make_party_accept_member_tester01")
        void accept_member_test01() throws Exception {
            ApplicationPartyDto.AcceptRequest request = ApplicationPartyDto.AcceptRequest
                    .builder()
                    .userID(userID)
                    .partyID(partyID)
                    .build();

            mockMvc.perform(
                    patch("/party/application")
                            .contentType("application/json")
                            .accept("application/json")
                            .content(objectMapper.writeValueAsString(request))
            ).andDo(print());
        }

        @Test
        @Order(3)
        @DisplayName("실패테스트02=파티원_전환_실패_존재하지않거나_권한이_없는_파티")
        @WithAccount("preprocessing_make_party_accept_member_tester01")
        void accept_member_test_failed01() throws Exception {
            ApplicationPartyDto.AcceptRequest request = ApplicationPartyDto.AcceptRequest
                    .builder()
                    .userID(userID)
                    .partyID(9999L)
                    .build();

            mockMvc.perform(
                    patch("/party/application")
                            .contentType("application/json")
                            .accept("application/json")
                            .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().is4xxClientError());
        }

        @Test
        @Order(3)
        @DisplayName("실패테스트03=파티원_전환_실패_없는_회원")
        @WithAccount("preprocessing_make_party_accept_member_tester01")
        void accept_member_test_failed02() throws Exception {
            ApplicationPartyDto.AcceptRequest request = ApplicationPartyDto.AcceptRequest
                    .builder()
                    .userID(List.of(1000L,1001L,1002L))
                    .partyID(partyID)
                    .build();

            mockMvc.perform(
                    patch("/party/application")
                            .contentType("application/json")
                            .accept("application/json")
                            .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().is4xxClientError());
        }
        @Test
        @Order(999)
        @DisplayName("데이터베이스 초기화")
        void init() {
            fileRepository.deleteAll();
            userPartyRepository.deleteAll();
            partyRepository.deleteAll();
            userRepository.deleteAll();
        }
    }
    @Nested
    @DisplayName("파티원_추방_및_신청_거절")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class BannedAndRejectMemberTest {
        public static long partyID;
        public static List<Long> userID_BR = new ArrayList<>();
        @Test
        @Order(1)
        @DisplayName("사전작업01=파티_생성")
        @WithAccount("preprocessing_make_party_reject_banned_tester01")
        void preprocessing_make_party_reject_banned_test() throws Exception{
            CreatePartyDto.Request request = makeParty("preprocess_party_title",
                    "preprocess_party_description");
            MvcResult result = mockMvc.perform(
                    post("/party")
                            .contentType("application/json")
                            .accept("application/json")
                            .content(objectMapper.writeValueAsString(request))
            ).andReturn();
            List<HashMap<String,Object>> td = (List<HashMap<String, Object>>) objectMapper.readValue(result.getResponse().getContentAsString(),HashMap.class).get("data");
            partyID = (long)(int)td.get(0).get("id");
        }

        @Test
        @Order(2)
        @DisplayName("사전작업02=파티_신청")
        @WithAccount("preprocessing_application_party_bannedAndReject_tester01")
        void preprocessing_application_party_test() throws Exception {
            ApplicationPartyDto.Request request = ApplicationPartyDto.Request.builder()
                    .partyId(partyID)
                    .partyName("preprocess_party_title")
                    .build();
            MvcResult result = mockMvc.perform(
                    post("/party/application")
                            .contentType("application/json")
                            .accept("application/json")
                            .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk()).andReturn();

            List<HashMap<String,Object>> data = (List<HashMap<String, Object>>) objectMapper.readValue(result.getResponse().getContentAsString(), HashMap.class).get("data");
            userID_BR.add((long)(int)data.get(0).get("userId"));
        }

        @Test
        @Order(3)
        @DisplayName("사전작업03=파티_가입_승인")
        @WithAccount("preprocessing_make_party_reject_banned_tester01")
        void preprocessing_accept_party() throws Exception{
            System.out.println("사전작업03=파티_가입_승인"+userID_BR.toString());
            ApplicationPartyDto.AcceptRequest request = ApplicationPartyDto.AcceptRequest
                    .builder()
                    .userID(userID_BR)
                    .partyID(partyID)
                    .build();

            mockMvc.perform(
                    patch("/party/application")
                            .contentType("application/json")
                            .accept("application/json")
                            .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isNoContent());
        }

        @Test
        @Order(4)
        @DisplayName("메인테스트01=파티원_추방_성공")
        @WithAccount("preprocessing_make_party_reject_banned_tester01")
        void banned_test_01() throws Exception{
            BannedMemberDto.Request request = BannedMemberDto.Request
                    .builder()
                    .partyID(partyID)
                    .userID(userID_BR)
                    .build();

            mockMvc.perform(
                    delete("/party/member")
                            .contentType("application/json")
                            .accept("application/json")
                            .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isNoContent());

            userID_BR.clear();
            System.out.println("클리어"+userID_BR.toString());
        }
        @Test
        @Order(5)
        @DisplayName("사전작업04=파티_재신청")
        @WithAccount("preprocessing_application_party_bannedAndReject_tester01")
        void preprocessing_application_party_test2() throws Exception {
            ApplicationPartyDto.Request request = ApplicationPartyDto.Request.builder()
                    .partyId(partyID)
                    .partyName("preprocess_party_title")
                    .build();
            MvcResult result = mockMvc.perform(
                    post("/party/application")
                            .contentType("application/json")
                            .accept("application/json")
                            .content(objectMapper.writeValueAsString(request))
            ).andReturn();

            List<HashMap<String,Object>> data = (List<HashMap<String, Object>>) objectMapper.readValue(result.getResponse().getContentAsString(), HashMap.class).get("data");
            userID_BR.add((long)(int)data.get(0).get("userId"));
        }

        @Test
        @Order(6)
        @DisplayName("사전작업05=파티_신청_거절")
        @WithAccount("preprocessing_make_party_reject_banned_tester01")
        void re_accept() throws Exception{
            System.out.println("파티원 추방 성공"+userID_BR.toString());
            BannedMemberDto.Request request = BannedMemberDto.Request
                    .builder()
                    .partyID(partyID)
                    .userID(userID_BR)
                    .build();

            mockMvc.perform(
                    delete("/party/member")
                            .contentType("application/json")
                            .accept("application/json")
                            .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isNoContent());

            Optional<UserParty> id = userPartyRepository.findById(2L);

            System.out.println(id.get().getIsDelete());
            userID_BR = new ArrayList<>();
        }

        @Test
        @Order(999)
        @DisplayName("데이터베이스_초기화")
        void init() {
            fileRepository.deleteAll();
            userPartyRepository.deleteAll();
            partyRepository.deleteAll();
            userRepository.deleteAll();
        }
    }

    @Nested
    @DisplayName("파티를_탈퇴한다")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class WithdrawTest {
        public static Long partyID;
        public static List<Long> userID = new ArrayList<>();
        @Test
        @Order(1)
        @DisplayName("사전작업01=파티_생성")
        @WithAccount("preprocessing_make_party_withdraw_tester01")
        void preprocessing_make_party_reject_banned_test() throws Exception{
            CreatePartyDto.Request request = makeParty("preprocess_party_title",
                    "preprocess_party_description");
            MvcResult result = mockMvc.perform(
                    post("/party")
                            .contentType("application/json")
                            .accept("application/json")
                            .content(objectMapper.writeValueAsString(request))
            ).andReturn();
            List<HashMap<String,Object>> td = (List<HashMap<String, Object>>) objectMapper.readValue(result.getResponse().getContentAsString(),HashMap.class).get("data");
            partyID = (long)(int)td.get(0).get("id");
        }

        @Test
        @Order(2)
        @DisplayName("사전작업02=파티_신청")
        @WithAccount("preprocessing_application_party_withdraw_tester01")
        void preprocessing_application_party_test() throws Exception {
            ApplicationPartyDto.Request request = ApplicationPartyDto.Request.builder()
                    .partyId(partyID)
                    .partyName("preprocess_party_title")
                    .build();
            MvcResult result = mockMvc.perform(
                    post("/party/application")
                            .contentType("application/json")
                            .accept("application/json")
                            .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk()).andReturn();

            List<HashMap<String,Object>> data = (List<HashMap<String, Object>>) objectMapper.readValue(result.getResponse().getContentAsString(), HashMap.class).get("data");
            userID.add((long)(int)data.get(0).get("userId"));
        }

        @Test
        @Order(3)
        @DisplayName("사전작업03=파티_가입_승인")
        @WithAccount("preprocessing_make_party_withdraw_tester01")
        void preprocessing_accept_party() throws Exception{
            ApplicationPartyDto.AcceptRequest request = ApplicationPartyDto.AcceptRequest
                    .builder()
                    .userID(userID)
                    .partyID(partyID)
                    .build();

            mockMvc.perform(
                    patch("/party/application")
                            .contentType("application/json")
                            .accept("application/json")
                            .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isNoContent());
        }

        @Test
        @Order(4)
        @DisplayName("메인테스트01=파티_탈퇴")
        @WithAccount("preprocessing_application_party_withdraw_tester01")
        void main_test01() throws Exception{
            mockMvc.perform(
                    delete("/party/member/my-parties")
                            .param("partyID",partyID.toString())
            ).andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("파티원의 등급을 변경한다.")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ChangeGradeTest {

        public static long partyID;
        public static List<Long> userID = new ArrayList<Long>();

        @Test
        @Order(1)
        @DisplayName("사전작업01=파티생성")
        @WithAccount("pre_modify_grade_maker01")
        void test01() throws Exception {
            CreatePartyDto.Request request = makeParty("preprocess_party_title",
                    "preprocess_party_description");
            MvcResult result = mockMvc.perform(
                    post("/party")
                            .contentType("application/json")
                            .accept("application/json")
                            .content(objectMapper.writeValueAsString(request))
            ).andReturn();
            List<HashMap<String,Object>> td = (List<HashMap<String, Object>>) objectMapper.readValue(result.getResponse().getContentAsString(),HashMap.class).get("data");
            partyID = (long)(int)td.get(0).get("id");
        }
        @Test
        @Order(2)
        @DisplayName("사전작업02=파티가입")
        @WithAccount("pre_modify_grade_application01")
        void preprocessing_application_party_test() throws Exception {
            ApplicationPartyDto.Request request = ApplicationPartyDto.Request.builder()
                    .partyId(partyID)
                    .partyName("preprocess_party_title")
                    .build();
            MvcResult result = mockMvc.perform(
                    post("/party/application")
                            .contentType("application/json")
                            .accept("application/json")
                            .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isOk()).andReturn();

            List<HashMap<String,Object>> data = (List<HashMap<String, Object>>) objectMapper.readValue(result.getResponse().getContentAsString(), HashMap.class).get("data");
            userID.add((long)(int)data.get(0).get("userId"));
        }

        @Test
        @Order(3)
        @DisplayName("사전작업03=파티_가입_승인")
        @WithAccount("pre_modify_grade_maker01")
        void preprocessing_accept_party() throws Exception{
            ApplicationPartyDto.AcceptRequest request = ApplicationPartyDto.AcceptRequest
                    .builder()
                    .userID(userID)
                    .partyID(partyID)
                    .build();

            mockMvc.perform(
                    patch("/party/application")
                            .contentType("application/json")
                            .accept("application/json")
                            .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isNoContent());
        }

        @Test
        @Order(4)
        @DisplayName("메인테스트01=파티원_등급_변경")
        @WithAccount("pre_modify_grade_maker01")
        void main_test_01() throws Exception{
            ModifyMemberGradeDto.ModifyMember inner = ModifyMemberGradeDto.ModifyMember.builder()
                    .grade(PartyMemberType.ADMIN)
                    .memberID(userID.get(0))
                    .build();
            ModifyMemberGradeDto.Request request = ModifyMemberGradeDto.Request.builder()
                    .members(List.of(inner))
                    .partyID(partyID)
                    .build();
            mockMvc.perform(
                    patch("/party/member")
                            .contentType("application/json")
                            .accept("application/json")
                            .content(objectMapper.writeValueAsString(request))
            ).andDo(print());
        }
    }
}