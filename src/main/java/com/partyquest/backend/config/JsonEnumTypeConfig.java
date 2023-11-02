package com.partyquest.backend.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.partyquest.backend.config.exception.EnumTypeNotMatchedException;
import com.partyquest.backend.config.exception.ErrorCode;

public class JsonEnumTypeConfig {

    public static <T extends Enum<?>> T fromString(Class<T> enumType, String s) {
        for(T type : enumType.getEnumConstants()) {
            if(type.name().equalsIgnoreCase(s)) {
                return type;
            }
        }
        throw new EnumTypeNotMatchedException(enumType.getName()+" is not a valid enum", ErrorCode.INTER_SERVER_ERROR);
    }
}
