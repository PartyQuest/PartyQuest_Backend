package com.partyquest.backend.service.impl;

import com.partyquest.backend.config.exception.*;
import com.partyquest.backend.domain.dto.PartyDto;
import com.partyquest.backend.domain.dto.RepositoryDto;
import com.partyquest.backend.domain.entity.File;
import com.partyquest.backend.domain.entity.Party;
import com.partyquest.backend.domain.entity.User;
import com.partyquest.backend.domain.entity.UserParty;
import com.partyquest.backend.domain.repository.*;
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
    private final QuestRepository questRepository;

    @Autowired
    public PartyServiceImpl(PartyRepository partyRepository,
                            UserRepository userRepository,
                            UserPartyRepository userPartyRepository,
                            FileRepository fileRepository,
                            QuestRepository questRepository) {
        this.partyRepository = partyRepository;
        this.userRepository = userRepository;
        this.userPartyRepository = userPartyRepository;
        this.fileRepository = fileRepository;
        this.questRepository = questRepository;
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
    }

    //검색 키워드, 파티이름, 파티장
    @Override
    public List<PartyDto.ReadPartyDto.Response> readPartyDto(String master, String title, Long id) {
        List<RepositoryDto.ReadPartiesVO> voList = partyRepository.getPartiesTmp(master, title, id);
        List<ReadPartyDto.Response> result = new ArrayList<>();
        for(RepositoryDto.ReadPartiesVO vo : voList) {
            result.add(
                    ReadPartyDto.Response.builder()
                            .title(vo.getPartyTitle())
                            .partyMaster(vo.getPartyMaster())
                            .capability((int)vo.getPartyMemberCnt())
                            .thumbnailPath(vo.getPartyThumbnailPath())
                            .id(vo.getPartyId())
                            .build()
            );
        }
        return result;
    }

    @Override
    public PartyDto.ReadPartyDto.Response readPartyDto(long id) {
        return null;
    }

    @Override
    public ApplicationPartyDto.Response ApplicationParty(ApplicationPartyDto.Request requestDto, long userID) {
        Optional<User> optUser = userRepository.findById(userID);
        Optional<Party> optParty = partyRepository.getParty(requestDto.getPartyId());
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
                .userId(user.getId())
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

    @Override
    public List<ReadPartyMemberDto.Response> getMemberFromGrade(Long partyId, PartyMemberType grade) {
        if(partyRepository.findById(partyId).isEmpty()) throw new PartyNotFoundException("NOT FOUND PARTY",ErrorCode.PARTY_NOT_FOUND);

        List<RepositoryDto.PartyMemberVO> memberFromGrade = userPartyRepository.findMemberFromGrade(partyId, grade);
        return memberFromGrade.stream()
                .map(member -> ReadPartyMemberDto.Response.builder()
                        .grade(member.getGrade())
                        .partyID(partyId)
                        .filePath(member.getFilePath())
                        .nickname(member.getNickname())
                        .registered(member.isRegistered())
                        .userID(member.getUserID())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<MembershipPartyDto.Response> getMembershipParties(long userId) {
        Optional<User> optUser = userRepository.findById(userId);
        if(optUser.isEmpty()) throw new EmailNotFoundException("NOT FOUND USER",ErrorCode.EMAIL_NOT_FOUND);
        List<RepositoryDto.MembershipDto> membershipDtoList = userPartyRepository.findMembershipUser(optUser.get());

        List<MembershipPartyDto.Response> result = new ArrayList<>();
        for(RepositoryDto.MembershipDto dto : membershipDtoList) {
            result.add(MembershipPartyDto.Response.builder()
                            .partyID(dto.getId())
                            .memberGrade(dto.getGrade().toString())
                    .partyMaster(dto.getPartyMaster())
                    .partyMemberCnt(dto.getPartyMemberCnt())
                    .partyThumbnailPath(dto.getPartyThumbnailPath())
                    .partyTitle(dto.getPartyTitle())
                    .build());
        }
        return result;
    }

    @Override
    public ReadPartyDto.Response readPartySpecification(Long id) {
        RepositoryDto.ReadPartyVO vo = partyRepository.getPartiesTmp(id);
        return ReadPartyDto.Response.builder()
                .thumbnailPath(vo.getPartyThumbnailPath())
                .description(vo.getDescription())
                .capability((int)vo.getPartyMemberCnt())
                .partyMaster(vo.getPartyMaster())
                .id(vo.getPartyId())
                .title(vo.getPartyTitle())
                .build();
    }

    /**
     * 파티가입 신청한 유저를 승인 처리하는 메소드
     * @param dto 클라이언트로 입력받은 데이터
     * @param masterID 요청한 클라이언트의 레퍼런스 아이디
     * @throws EmailNotFoundException 회원 검증
     * @throws NotPartyMemberException 신청자 검증
     * @throws NotAdminException 요청자 등급 검증
     */
    @Override
    public void AcceptPartyApplicator(ApplicationPartyDto.AcceptRequest dto, long masterID) {
        //입력 값 - 유저 데이터 검증
        CheckIsUser(dto.getUserID());

        //유저 데이터가 정확히 해당 파티를 신청했는지 검증 -> userParty 테이블에서 dto.getPartyID로 파티 일치하는지 조회
        if(!userPartyRepository.isApplicationUser(dto.getUserID(),dto.getPartyID()))
            throw new NotPartyMemberException("NOT PARTY MEMBER",ErrorCode.NOT_PARTY_MEMBER);

        //파티 마스터가 정확히 권한을 가지고 있는 유저인가?
        CheckIsAdmin(masterID,dto.getPartyID());


        userPartyRepository.updateAcceptApplicator(dto.getUserID());
    }

    /**
     * 파티 멤버를 추방하거나 신청한 유저를 거절하는 메소드
     * @param dto 클라이언트로 입력받은 데이터
     * @param masterID 요청한 클라이언트의 레퍼런스 아이디
     * @throws EmailNotFoundException 회원검증
     * @throws NotAdminException 요청자 등급 검증
     * @throws PartyMemberException 대상 멤버 유효성 검증
     * */
    @Override
    public void BannedAndRejectPartyMember(BannedMemberDto.Request dto, long masterID) {
        CheckIsUser(dto.getUserID());
        CheckIsAdmin(masterID, dto.getPartyID());
        CheckPartyMember(dto.getUserID(),dto.getPartyID());
        userPartyRepository.updateRegisterAndisDeleteFalse(dto.getPartyID(),dto.getUserID());
    }

    @Override
    public void WithdrawParty(long userID, Long partyID) {
        CheckIsUser(List.of(userID));
        CheckIsParty(partyID);
        CheckPartyMember(List.of(userID),partyID);
        userPartyRepository.updateRegisterAndisDeleteFalse(partyID, List.of(userID));
    }

    @Override
    public void ModifyPartyMemberGrade(long userID, ModifyMemberGradeDto.Request dto) {
        CheckIsUser(List.of(userID));
        CheckIsAdmin(userID,dto.getPartyID());
        CheckIsParty(dto.getPartyID());

        List<Long> memberIDs = dto.getMembers()
                .stream()
                .map(ModifyMemberGradeDto.ModifyMember::getMemberID)
                .collect(Collectors.toList());

        CheckIsUser(memberIDs);
        CheckPartyMember(memberIDs,dto.getPartyID());

        //검증 끝
        for (ModifyMemberGradeDto.ModifyMember member : dto.getMembers()) {
            userPartyRepository.updateUserPartyMemberGrade(dto.getPartyID(),member.getMemberID(),member.getGrade());
        }
    }

    @Override
    public void ModifyPartySpecification(long userID, ModifyPartySpecificationDto.Request dto) {
        CheckIsUser(List.of(userID));
        CheckPartyMember(List.of(userID),dto.getPartyID());
        CheckIsAdmin(userID,dto.getPartyID());

        Party party = partyRepository.findById(dto.getPartyID())
                .orElseThrow(() -> new PartyNotFoundException("NOT FOUND PARTY", ErrorCode.PARTY_NOT_FOUND));

        party.setDescription(dto.getDescription());
        party.setTitle(dto.getTitle());
    }

    @Override
    public void DeleteParty(long userID, Long partyID) {
        CheckIsUser(List.of(userID));
        CheckIsParty(partyID);
        CheckIsMaster(userID, partyID);

        //소속 파티원 isDeleted => TRUE로 변경
        userPartyRepository.updateIsDeletePartyMember(partyID);
        //해당 파티의 퀘스트들 isDelete => TRUE로 변경
        questRepository.updateIsDeleteQuestFromPartyID(partyID);
        //해당 파티의 파일들 isDelete => TRUE로 변경
        fileRepository.updateIsDeletedFromPartyID(partyID);
        //파티 isDelete => TRUE로 변경
        partyRepository.updateIsDeleteFromParty(partyID);
        //TODO 파티 삭제 푸시알림 구현

    }

    private void CheckIsUser(List<Long> userID) {
        if(!userRepository.isUser(userID))
            throw new EmailNotFoundException("NOT FOUND USER",ErrorCode.EMAIL_NOT_FOUND);
    }

    private void CheckIsAdmin(Long masterID, Long partyID) {
        if(!userPartyRepository.isMasterAndAdminUserTmp(masterID, partyID))
            throw new NotAdminException("NOT ADMIN USER",ErrorCode.ACCESS_DENIED);
    }
    private void CheckPartyMember(List<Long> userID, Long partyID) {
        if(!userPartyRepository.existsByUsers(userID,partyID))
            throw new PartyMemberException("NOT PARTY MEMBER",ErrorCode.PARTY_MEMBER_ERROR);
    }

    private void CheckIsParty(Long partyID) {
        if(!partyRepository.existsById(partyID)) {
            throw new PartyNotFoundException("NOT FOUND PARTY",ErrorCode.PARTY_NOT_FOUND);
        }
    }

    private void CheckIsMaster(Long masterID, Long partyID) {
        if(!userPartyRepository.existsMasterFromUserParty(masterID, partyID)) {
            throw new NotAdminException("NOT MASTER", ErrorCode.ACCESS_DENIED);
        }
    }
}
