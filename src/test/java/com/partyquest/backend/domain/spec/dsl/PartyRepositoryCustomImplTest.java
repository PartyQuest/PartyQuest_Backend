package com.partyquest.backend.domain.spec.dsl;

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
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PartyRepositoryCustomImplTest {

    @Autowired
    PartyRepository partyRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserPartyRepository userPartyRepository;
    @Autowired
    FileRepository fileRepository;

    @Test
    @DisplayName("GET PARTIES TEST")
    void getPartiesTest() {
        User user = User.builder()
                .sns("LOCAL")
                .email("email")
                .nickname("nickname")
                .userParties(new LinkedList<>())
                .password("password")
                .deviceTokens(null)
                .build();
        User save1 = userRepository.save(user);

        Party party = null;
        for (int i = 0; i < 10; i++) {
            party = Party.builder()
                    .capabilities(20)
                    .accessCode("asdf")
                    .title("title"+i)
                    .isPublic(true)
                    .description("description")
                    .files(new LinkedList<>())
                    .build();
            party.setIsDeleted(false);
            Party save = partyRepository.save(party);

            for (int j = 0; j <2; j++) {
                File file = File.builder()
                        .type(FileType.PARTY_THUMBNAIL)
                        .errMsg("errMsg")
                        .party(save)
                        .filePath("path")
                        .fileOriginalName("origin")
                        .fileAttachChngName("name")
                        .build();
                fileRepository.save(file);
                save.getFiles().add(file);
            }

            UserParty userParty = UserParty.builder()
                    .user(save1)
                    .party(save)
                    .registered(true)
                    .memberGrade(PartyMemberType.MASTER)
                    .partyAdmin(true)
                    .build();

            userPartyRepository.save(userParty);
        }

        List<Party> parties = partyRepository.getParties(null,null,null);
        System.out.println(parties.size());
        User user1 = parties.get(0).getUserParties().get(0).getUser();
        //File file = parties.get(0).getFiles().get(0);
        //System.out.println(parties.get(0).getFiles().size());
        //List<File> files = fileRepository.findByParty(parties.get(0));

        assertAll(
                () -> assertEquals(user1.getNickname(), "nickname"),
                () -> assertEquals(user1.getSns(), "LOCAL"),
                () -> assertEquals(user1.getPassword(), "password")
//                () -> assertEquals(parties.get(0).getFiles().size(), 2)
        );
    }
}