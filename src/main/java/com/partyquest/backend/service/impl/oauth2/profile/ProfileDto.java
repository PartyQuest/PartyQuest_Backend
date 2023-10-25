package com.partyquest.backend.service.impl.oauth2.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDto {
    private String email;
    private String nickname;
    private String provider;
}
