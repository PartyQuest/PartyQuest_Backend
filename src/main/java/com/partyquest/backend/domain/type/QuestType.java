package com.partyquest.backend.domain.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.partyquest.backend.config.JsonEnumTypeConfig;

public enum QuestType {
    SUBMIT,NOTIFICATION;

    @JsonCreator
    public static QuestType fromString(String s) {
        return JsonEnumTypeConfig.fromString(QuestType.class,s);
    }
}
