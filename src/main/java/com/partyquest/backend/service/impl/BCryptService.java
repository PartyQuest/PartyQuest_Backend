package com.partyquest.backend.service.impl;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptService {
    public String encodeBcrypt(String plain) {
        return new BCryptPasswordEncoder().encode(plain);
    }
    public Boolean matchesBcrypt(String plain, String hashCode) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(plain, hashCode);
    }
}
