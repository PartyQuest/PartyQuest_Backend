package com.partyquest.backend.service.impl;

import com.partyquest.backend.config.exception.EmailNotFoundException;
import com.partyquest.backend.config.exception.ErrorCode;
import com.partyquest.backend.config.exception.PartyApplicationDuplicateException;
import com.partyquest.backend.config.exception.PartyNotFoundException;
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
import com.partyquest.backend.service.logic.PartyService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.partyquest.backend.domain.dto.PartyDto.*;

@Service
@Slf4j
public class PartyServiceImpl implements PartyService {

    private final PartyRepository partyRepository;
    private final UserRepository userRepository;
    private final UserPartyRepository userPartyRepository;
    private final FileRepository fileRepository;

    @Autowired
    public PartyServiceImpl(PartyRepository partyRepository,
                            UserRepository userRepository,
                            UserPartyRepository userPartyRepository,
                            FileRepository fileRepository) {
        this.partyRepository = partyRepository;
        this.userRepository = userRepository;
        this.userPartyRepository = userPartyRepository;
        this.fileRepository = fileRepository;
    }

    private User getUserData(long makerId) {
        Optional<User> optional = userRepository.findById(makerId);
        if(optional.isPresent()) return optional.get();
        else throw new EmailNotFoundException("USER NOT FOUND", ErrorCode.INTER_SERVER_ERROR);
    }

    @Override
    @Transactional
    public PartyDto.CreatePartyDto.Response createParty(PartyDto.CreatePartyDto.Request request, long makerID) {
        User user = getUserData(makerID);
        Party party = partyRepository.save(CreatePartyDto.Request.dtoToEntity(request));

        UserParty userParty = UserParty.builder()
                .user(user)
                .registered(true)
                .memberGrade(PartyMemberType.MASTER)
                .party(party)
                .partyAdmin(true)
                .build();

        userPartyRepository.save(userParty);

        party.getUserParties().add(userParty);
        user.getUserParties().add(userParty);

        File file = File.builder()
                .party(party)
                .fileAttachChngName("test code")
                .filePath("test path")
                .fileOriginalName("test origin")
                .type(FileType.PARTY_THUMBNAIL)
                .fileSize(1234)
                .build();

        fileRepository.save(file);
        party.getFiles().add(file);

        return PartyDto.CreatePartyDto.Response.entityToDto(party);



//
//        //Party Entity 생성
//        Party party = PartyDto.CreatePartyDto.Request.dtoToEntity(request);
//        Party partySave = partyRepository.save(party);
//        //파티 생성자(마스터) 계정정보 호출
//        User user;
//        Optional<User> optionalUser = userRepository.findById(makerID);
//        if(optionalUser.isEmpty()) throw new EmailNotFoundException("USER NOT FOUND", ErrorCode.EMAIL_NOT_FOUND);
//        else user = optionalUser.get();
//
//        //연결 테이블 UserParty 계정-파티 연결
//        UserParty userParty = UserParty.builder()
//                .user(user)
//                .party(partySave)
//                .partyAdmin(true)
//                .memberGrade(PartyMemberType.MASTER)
//                .registered(true)
//                .build();
//        UserParty save = userPartyRepository.save(userParty);
//
//        // User - UserParty - Party 연결
//        //User 연결
//        List<UserParty> parties = user.getUserParties();
//        parties.add(save);
//        user.setUserParties(parties);
//
////        party 연결
//        List<UserParty> partySet = party.getUserParties();
//        partySet.add(save);
//        party.setUserParties(partySet);
//
//        //[TODO] 이미지 결정해서 서버에 추가해야함
//        File file = File.builder()
//                .type(FileType.PARTY_THUMBNAIL)
//                .fileOriginalName("TEMP ORIGINAL NAME")
//                .filePath("TEMP PATH")
//                .fileAttachChngName("TEMP")
//                .party(partySave)
//                .build();
//
//        return PartyDto.CreatePartyDto.Response.entityToDto(party);
    }

    //검색 키워드, 파티이름, 파티장
    @Override
    public List<PartyDto.ReadPartyDto.Response> readPartyDto(String master, String title, Long id) {
        List<Party> parties = partyRepository.getParties(master, title, id);
        return parties.stream()
                .map(party -> {
                    String thumbnailPath = party.getFiles().stream()
                            .filter(file -> file.getType() == FileType.PARTY_THUMBNAIL)
                            .map(File::getFilePath)
                            .findFirst()
                            .orElse(null);

                    String partyMaster = party.getUserParties().stream()
                            .filter(userParty -> userParty.getMemberGrade() == PartyMemberType.MASTER)
                            .map(UserParty::getUser)
                            .filter(user -> master == null || user.getNickname().equals(master))
                            .map(User::getNickname)
                            .findFirst().orElse(null);

                    log.info(partyMaster);

                    return ReadPartyDto.Response.builder()
                            .thumbnailPath(thumbnailPath)
                            .id(party.getId())
                            .partyMaster(partyMaster)
                            .capability(party.getCapabilities())
                            .description(party.getDescription())
                            .title(party.getTitle())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public PartyDto.ReadPartyDto.Response readPartyDto(long id) {
        return null;
    }

    @Override
    public ApplicationPartyDto.Response ApplicationParty(ApplicationPartyDto.Request requestDto) {
        Optional<User> optUser = userRepository.findById(requestDto.getUserId());
        Optional<Party> optParty = partyRepository.getParty(requestDto.getPartId());
        if(optUser.isEmpty()) throw new EmailNotFoundException("NOT FOUND USER",ErrorCode.EMAIL_NOT_FOUND);
        if(optParty.isEmpty()) throw new PartyNotFoundException("NOT FOUND PARTY",ErrorCode.PARTY_NOT_FOUND);

        User user = optUser.get();
        Party party = optParty.get();

        Optional<UserParty> optUserParty = userPartyRepository.existEntryUser(party,user);
        UserParty userParty;
        if(optUserParty.isPresent()) {
            userParty = optUserParty.get();
            log.info(userParty.getMemberGrade().toString());
            if(userParty.getIsDelete()) {
                userParty.setIsDelete(false);
                userPartyRepository.save(userParty);
            } else {
                throw new PartyApplicationDuplicateException(ErrorCode.PARTY_APPLICATION_DUPLICATED,"ALREADY APPLICATED USER");
            }
        } else {
            userParty = UserParty.builder()
                    .partyAdmin(false)
                    .party(party)
                    .user(user)
                    .registered(false)
                    .memberGrade(PartyMemberType.NO_MEMBER)
                    .build();
            userParty = userPartyRepository.save(userParty);

            user.getUserParties().add(userParty);
            party.getUserParties().add(userParty);
        }

        ApplicationPartyDto.Response response = ApplicationPartyDto.Response.builder()
                .partyId(party.getId())
                .userPartyId(userParty.getId())
                .userId(userParty.getId())
                .build();

        return response;
    }

    @Override
    public boolean deleteParty(List<Long> partyIds) {
        try {
            for (Long partyId : partyIds) {
                Optional<Party> party = partyRepository.findById(partyId);
                if (party.isPresent()) {
                    Party tmp = party.get();
                    tmp.setIsDelete(true);
                    partyRepository.save(tmp);
                }

            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
