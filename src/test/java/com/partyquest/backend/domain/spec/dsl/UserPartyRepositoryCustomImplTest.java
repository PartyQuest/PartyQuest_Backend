package com.partyquest.backend.domain.spec.dsl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.partyquest.backend.domain.dto.RepositoryDto;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@SpringBootTest
public class UserPartyRepositoryCustomImplTest {
    @Autowired
    UserPartyRepository userPartyRepository;
    @Autowired
    PartyRepository partyRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("QUERY_TEST_01")
    void testQuery() {
        Party party = partyRepository.save(Party.builder()
                        .description("The name of the des")
                        .files(new LinkedList<>())
                        .isPublic(true)
                        .title("title")
                        .capabilities(20)
                        .build());
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

        UserParty userParty = userPartyRepository.save(
                UserParty.builder()
                        .partyAdmin(true)
                        .user(user)
                        .registered(true)
                        .memberGrade(PartyMemberType.MASTER)
                        .party(party)
                        .build());

        File file = fileRepository.save(
                File.builder()
                        .fileName("test")
                        .type(FileType.USER_THUMBNAIL)
                        .build());
        user.getFiles().add(file);

        System.out.println(user.getId());

//        List<RepositoryDto.UserApplicatorRepositoryDto> members = userPartyRepository.findMemberFromGrade(party, PartyMemberType.MASTER);
//        for(RepositoryDto.UserApplicatorRepositoryDto member : members) {
//            System.out.println(member.toString());
//        }
    }

    @Test
    @DisplayName("QUERY_TEST_02")
    void QUERY_TEST_02() throws JsonProcessingException {

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
                        .fileName("test")
                        .type(FileType.USER_THUMBNAIL)
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
                            .fileName("test")
                            .type(FileType.USER_THUMBNAIL)
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
        }

        List<RepositoryDto.MembershipDto> membershipUser = userPartyRepository.findMembershipUser(user);
        System.out.println(objectMapper.writeValueAsString(membershipUser));

        userRepository.deleteAll();
        fileRepository.deleteAll();
        userPartyRepository.deleteAll();
        partyRepository.deleteAll();
    }
}
