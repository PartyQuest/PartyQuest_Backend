package com.partyquest.backend.service.impl;

import com.partyquest.backend.config.WithAccount;
import com.partyquest.backend.domain.dto.AuthDto;
import com.partyquest.backend.domain.dto.PartyDto;
import com.partyquest.backend.domain.entity.File;
import com.partyquest.backend.domain.entity.Party;
import com.partyquest.backend.domain.entity.User;
import com.partyquest.backend.domain.entity.UserParty;
import com.partyquest.backend.domain.repository.FileRepository;
import com.partyquest.backend.domain.repository.PartyRepository;
import com.partyquest.backend.domain.repository.UserPartyRepository;
import com.partyquest.backend.domain.repository.UserRepository;
import com.partyquest.backend.domain.type.FileType;
import com.partyquest.backend.domain.type.PartyMemberType;
import com.partyquest.backend.service.logic.AuthService;
import com.partyquest.backend.service.logic.PartyService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
    @Autowired
    UserRepository userRepository;
    @Autowired
    FileRepository fileRepository;

    @Test
    @DisplayName("CREATE_PARTY")
    void CreateParty() {
        System.out.println("USER 관련 시작");
        AuthDto.SignupResponseDto dto = authService.SignUp(
                AuthDto.SignupDto.builder()
                        .password("password")
                        .nickname("nickname")
                        .email("email1")
                        .build(),
                "LOCAL");

        long id = authService.getUserByEmail("email1").getId();
        System.out.println("USER 관련 종료");
        System.out.println();
        System.out.println("PARTY 생성 관련 시작");
        partyService.createParty(PartyDto.CreatePartyDto.Request.builder()
                        .description("description")
                        .isPublic(true)
                        .title("title1")
                .build(),id);
        System.out.println("PARTY 생성 관련 종료");
        System.out.println();
        System.out.println("PARTY 검색 관련 시작");
        List<Party> parties = partyRepository.getParties(null, null, null);
        for(Party p: parties) {
            System.out.println(p.getFiles().get(0).getFilePath());
        }
        System.out.println("PARTY 검색 관련 종료");
        

//        Party result = partyRepository.findByTitle("title1").get(0);
//        UserParty userParty = UserParty.builder()
//                .partyAdmin(true)
//                .party(result)
//                .memberGrade(PartyMemberType.MASTER)
//                .registered(true)
//                .user(authService.getUserByEmail("email1"))
//                .build();
//        userPartyRepository.save(userParty);

        //System.out.println(userPartyRepository.findByParty(result).getMemberGrade());

//        assertAll(
//                () -> assertEquals(result.getTitle(), "title1"),
//                () -> assertEquals(result.getCapabilities(), 20),
//                () -> assertEquals(result.getDescription(), "description")
//        );

        partyRepository.deleteAll();
        userRepository.deleteAll();
        userPartyRepository.deleteAll();
    }

    @Test
    @DisplayName("DELETE_PARTY")
    @WithAccount("email")
    void deleteParty() {
        Optional<User> byId = userRepository.findById(1L);
        User user = null;
        if(byId.isPresent()) user = byId.get();


        Party party = null;
        for (int i = 0; i < 10; i++) {
            party = Party.builder()
                    .capabilities(20)
                    .accessCode("asdf")
                    .title("title"+i)
                    .isPublic(true)
                    .description("description")
                    .files(null)
                    .build();
            party.setIsDeleted(false);
            partyRepository.save(party);

            UserParty userParty = UserParty.builder()
                    .user(user)
                    .party(party)
                    .registered(true)
                    .memberGrade(PartyMemberType.MASTER)
                    .partyAdmin(true)
                    .build();

            userPartyRepository.save(userParty);
        }

        List<Party> parties = partyRepository.getParties(null,null,null);
        System.out.println(parties.size());

        List<Long> ids = List.of(1L,2L,3L,4L,5L,6L,7L,8L,9L,10L);
        partyService.deleteParty(ids);

        List<Party> parties2 = partyRepository.getParties(null,null,null);
        System.out.println(parties2.size());

        userRepository.deleteAll();
        partyRepository.deleteAll();
        userPartyRepository.deleteAll();
        fileRepository.deleteAll();
    }

    @Test
    @DisplayName("READ_PARTY")
    void readParty() {
        User user = User.builder()
                .sns("LOCAL")
                .email("email")
                .nickname("nickname")
                .userParties(new LinkedList<>())
                .password("password")
                .deviceTokens(null)
                .build();
        userRepository.save(user);

        Party party = null;
        for (int i = 0; i < 10; i++) {
            party = Party.builder()
                    .capabilities(20)
                    .accessCode("asdf")
                    .title("title"+i)
                    .isPublic(true)
                    .description("description")
                    .files(null)
                    .build();
            party.setIsDeleted(false);
            partyRepository.save(party);
            for (int j = 0; j < 1; j++) {
                File file = File.builder()
                        .errMsg("errMsg")
                        .party(party)
                        .filePath("path"+j)
                        .fileOriginalName("origin")
                        .fileAttachChngName("name")
                        .type(FileType.PARTY_THUMBNAIL)
                        .build();
                fileRepository.save(file);
            }

            UserParty userParty = UserParty.builder()
                    .user(user)
                    .party(party)
                    .registered(true)
                    .memberGrade(PartyMemberType.MASTER)
                    .partyAdmin(true)
                    .build();

            userPartyRepository.save(userParty);
        }

        List<PartyDto.ReadPartyDto.Response> responses = partyService.readPartyDto(null, null, null);
        for(PartyDto.ReadPartyDto.Response response : responses) {
            System.out.println(response.toString());
        }
//        assertAll(
//                () -> assertEquals(responses.get(0).getThumbnailPath(),"path0"),
//                () -> assertEquals(responses.get(0).getCapability(), 20),
//                () -> assertEquals(responses.get(0).getPartyMaster(), "nickname")
//        );

        userRepository.deleteAll();
        partyRepository.deleteAll();
        userPartyRepository.deleteAll();
        fileRepository.deleteAll();
    }

    @Test
    @DisplayName("APPLICATION_PARTY_SERVICE")
    @Transactional
    void ApplicationParty() {
        AuthDto.SignupResponseDto signupResponseDto1 = authService.SignUp(AuthDto.SignupDto.builder()
                .email("tester01")
                .nickname("test01")
                .password("pass")
                .build(), "LOCAL");
        AuthDto.SignupResponseDto signupResponseDto2 = authService.SignUp(AuthDto.SignupDto.builder()
                .email("tester02")
                .nickname("test02")
                .password("pass")
                .build(), "LOCAL");
        Party party = new Party(0L, "testCode", "title", "test", 20, true, new ArrayList<>(), null);
        party = partyRepository.save(party);

        User user1 = userRepository.findById(signupResponseDto1.getId()).get();
        User user2 = userRepository.findById(signupResponseDto2.getId()).get();

        UserParty userParty = UserParty.builder()
                .memberGrade(PartyMemberType.MASTER)
                .partyAdmin(true)
                .party(party)
                .registered(true)
                .user(user1)
                .build();

        userPartyRepository.save(userParty);

        user1.getUserParties().add(userParty);
        party.getUserParties().add(userParty);


        PartyDto.ApplicationPartyDto.Response response = partyService.ApplicationParty(PartyDto.ApplicationPartyDto.Request.builder()
                .partyName("title")
                .partId(party.getId())
                .userId(user2.getId())
                .build());

        System.out.println(response.getPartyId() + " " + response.getUserPartyId() + " " + response.getUserId());
        List<UserParty> testParty = userPartyRepository.findByParty(party);
        for(UserParty up : testParty) {
            System.out.println(up.getUser().getEmail() + " : " + up.getMemberGrade() + "\t"+"REGISTERED: "+up.isRegistered());
        }
        Long member = userPartyRepository.countPartyMember(party);
        System.out.println(member);
    }

    @Test
    @DisplayName("MEMBERSHIP_USER_SERVICE_TEST")
    void membershipUserTest() {

        User user = userRepository.save(
                User.builder()
                        .files(new LinkedList<>())
                        .nickname("user")
                        .email("user@example.com")
                        .password("password")
                        .deviceTokens(new LinkedList<>())
                        .userParties(new LinkedList<>())
                        .sns("LOCAL")
                        .build());
        File file = fileRepository.save(
                File.builder()
                        .fileSize(1234L)
                        .type(FileType.USER_THUMBNAIL)
                        .fileOriginalName("testOriginName")
                        .filePath("testFilePath")
                        .fileAttachChngName("testAttachChngName")
                        .user(user)
                        .build());
        user.getFiles().add(file);
        for(int i = 0; i < 10; i++) {
            Party party = partyRepository.save(Party.builder()
                    .description("The name of the des")
                    .files(new LinkedList<>())
                    .isPublic(true)
                    .title("title"+i)
                    .capabilities(20)
                    .build());

            File file2 = fileRepository.save(
                    File.builder()
                            .fileSize(1234L)
                            .type(FileType.PARTY_THUMBNAIL)
                            .fileOriginalName("testOriginName")
                            .filePath("testFilePath1234")
                            .fileAttachChngName("testAttachChngName")
                            .party(party)
                            .build());
            party.getFiles().add(file2);

            UserParty userParty = userPartyRepository.save(
                    UserParty.builder()
                            .partyAdmin(true)
                            .user(user)
                            .registered(true)
                            .memberGrade(PartyMemberType.MASTER)
                            .party(party)
                            .build());
            UserParty userParty2 = userPartyRepository.save(
                    UserParty.builder()
                            .partyAdmin(true)
                            .user(user)
                            .registered(true)
                            .memberGrade(PartyMemberType.MASTER)
                            .party(party)
                            .build());
        }

        List<PartyDto.MembershipPartyDto.Response> list = partyService.getMembershipParties(user.getId());
        System.out.println(list.toString());
    }
}