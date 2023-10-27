package com.partyquest.backend.service.impl;

import com.partyquest.backend.config.exception.EmailNotFoundException;
import com.partyquest.backend.config.exception.ErrorCode;
import com.partyquest.backend.domain.dto.PartyDto;
import com.partyquest.backend.domain.entity.Party;
import com.partyquest.backend.domain.entity.User;
import com.partyquest.backend.domain.entity.UserParty;
import com.partyquest.backend.domain.repository.PartyRepository;
import com.partyquest.backend.domain.repository.UserPartyRepository;
import com.partyquest.backend.domain.repository.UserRepository;
import com.partyquest.backend.domain.type.PartyMemberType;
import com.partyquest.backend.service.logic.PartyService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class PartyServiceImpl implements PartyService {

    private final PartyRepository partyRepository;
    private final UserRepository userRepository;
    private final UserPartyRepository userPartyRepository;

    @Autowired
    public PartyServiceImpl(PartyRepository partyRepository,
                            UserRepository userRepository,
                            UserPartyRepository userPartyRepository) {
        this.partyRepository = partyRepository;
        this.userRepository = userRepository;
        this.userPartyRepository = userPartyRepository;
    }

    @Override
    @Transactional
    public PartyDto.CreatePartyDto.Response createPartyDto(PartyDto.CreatePartyDto.Request request, long makerID) {

        //Party Entity 생성
        Party party = PartyDto.CreatePartyDto.Request.dtoToEntity(request);
        Party partySave = partyRepository.save(party);
        //파티 생성자(마스터) 계정정보 호출
        User user;
        Optional<User> optionalUser = userRepository.findById(makerID);
        if(optionalUser.isEmpty()) throw new EmailNotFoundException("USER NOT FOUND", ErrorCode.EMAIL_NOT_FOUND);
        else user = optionalUser.get();

        //연결 테이블 UserParty 계정-파티 연결
        UserParty userParty = UserParty.builder()
                .user(user)
                .party(party)
                .partyAdmin(true)
                .memberGrade(PartyMemberType.MASTER)
                .registered(true)
                .build();
        UserParty save = userPartyRepository.save(userParty);

        // User - UserParty - Party 연결
        //User 연결
        List<UserParty> parties = user.getUserParties();
        parties.add(save);
        user.setUserParties(parties);


//        party 연결
        List<UserParty> partySet = party.getUserParties();
        partySet.add(save);
        party.setUserParties(partySet);



        return PartyDto.CreatePartyDto.Response.entityToDto(party);
    }
}
