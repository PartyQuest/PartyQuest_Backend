package com.partyquest.backend.domain.dto;

import com.partyquest.backend.domain.type.FileType;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestDto {
    //private String type;
    private FileType type;
}
