package com.partyquest.backend.domain.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.partyquest.backend.config.JsonEnumTypeConfig;
import com.partyquest.backend.config.exception.EnumTypeNotMatchedException;
import com.partyquest.backend.config.exception.ErrorCode;

public enum LoginType {
    LOCAL,KAKAO,NAVER,GOOGLE,APPLE;

    @JsonCreator
    public static LoginType fromString(String s) {
        return JsonEnumTypeConfig.fromString(LoginType.class,s);
    }
}
