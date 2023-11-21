package com.partyquest.backend.domain.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.partyquest.backend.config.JsonEnumTypeConfig;
import com.partyquest.backend.config.exception.EnumTypeNotMatchedException;
import com.partyquest.backend.config.exception.ErrorCode;

public enum PartyMemberType {
    MASTER,ADMIN,MEMBER,NO_MEMBER;

    @JsonCreator
    public static PartyMemberType fromString(String s) {
        return JsonEnumTypeConfig.fromString(PartyMemberType.class,s);
    }
}
