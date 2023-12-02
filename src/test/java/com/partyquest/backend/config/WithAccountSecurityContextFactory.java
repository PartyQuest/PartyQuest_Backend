package com.partyquest.backend.config;

import com.partyquest.backend.config.jwt.TokenProvider;
import com.partyquest.backend.domain.entity.User;
import com.partyquest.backend.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.ArrayList;
import java.util.Optional;

public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenProvider tokenProvider;

    @Override
    public SecurityContext createSecurityContext(WithAccount annotation) {
        Optional<User> optUser = userRepository.findByEmail(annotation.value());
        User user;
        if(optUser.isEmpty()) {
            user = userRepository.save(createUser(annotation.value()));
        } else {
            user = optUser.get();
        }
        String[] token = tokenProvider.createToken(user).split("::");

        AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.getId(),null, AuthorityUtils.NO_AUTHORITIES);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }

    private User createUser(String email) {
            return User.builder()
                    .password("password")
                    .nickname(email)
                    .email(email)
                    .sns("LOCAL")
                    .userParties(new ArrayList<>())
                    .deviceTokens(new ArrayList<>())
                    .files(new ArrayList<>())
                    .build();
    }
}
