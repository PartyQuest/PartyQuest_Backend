package com.partyquest.backend.domain.spec.dsl;

import com.partyquest.backend.domain.dto.PartyDto;
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

            File file = File.builder()
                    .type(FileType.PARTY_THUMBNAIL)
                    .errMsg("errMsg")
                    .party(save)
                    .filePath("path"+i)
                    .fileOriginalName("origin")
                    .fileAttachChngName("name")
                    .build();
            fileRepository.save(file);
            save.getFiles().add(file);


            UserParty userParty = UserParty.builder()
                    .user(save1)
                    .party(save)
                    .registered(true)
                    .memberGrade(PartyMemberType.MASTER)
                    .partyAdmin(true)
                    .build();

            userPartyRepository.save(userParty);
        }

        List<RepositoryDto.ReadPartiesVO> tmp = partyRepository.getPartiesTmp(null, null, null);
        //System.out.println(tmp.toString());
        for(RepositoryDto.ReadPartiesVO dto : tmp) {
            System.out.println(dto.toString());
        }
        System.out.println();
//        RepositoryDto.ReadPartyVO vo = partyRepository.getPartiesTmp(1);
//        System.out.println(vo.toString());

        userRepository.deleteAll();
        fileRepository.deleteAll();
        userPartyRepository.deleteAll();
        partyRepository.deleteAll();
    }
}