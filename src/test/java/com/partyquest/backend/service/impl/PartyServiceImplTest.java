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
        UserParty userParty = UserParty.builder()
                .partyAdmin(true)
                .party(result)
                .memberGrade(PartyMemberType.MASTER)
                .registered(true)
                .user(authService.getUserByEmail("email1"))
                .build();
        userPartyRepository.save(userParty);

        //System.out.println(userPartyRepository.findByParty(result).getMemberGrade());

        assertAll(
                () -> assertEquals(result.getTitle(), "title1"),
                () -> assertEquals(result.getCapabilities(), 20),
                () -> assertEquals(result.getDescription(), "description")
        );

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
                .birth("birth")
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
        assertAll(
                () -> assertEquals(responses.get(0).getThumbnailPath(),"path0"),
                () -> assertEquals(responses.get(0).getCapability(), 20),
                () -> assertEquals(responses.get(0).getPartyMaster(), "nickname")
        );

        userRepository.deleteAll();
        partyRepository.deleteAll();
        userPartyRepository.deleteAll();
        fileRepository.deleteAll();
    }
}