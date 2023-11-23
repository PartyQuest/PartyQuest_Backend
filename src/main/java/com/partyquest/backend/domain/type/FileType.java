package com.partyquest.backend.domain.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.partyquest.backend.config.JsonEnumTypeConfig;

public enum FileType {
    PARTY_THUMBNAIL, QUEST, USER_THUMBNAIL;

    @JsonCreator
    public static FileType fromString(String value) {
        return JsonEnumTypeConfig.fromString(FileType.class,value);
    }
}
