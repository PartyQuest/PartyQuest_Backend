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

public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenProvider tokenProvider;

    @Override
    public SecurityContext createSecurityContext(WithAccount annotation) {
        User user = userRepository.save(createUser(annotation.value()));
        String[] token = tokenProvider.createToken(user).split("::");

        AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.getId(),null, AuthorityUtils.NO_AUTHORITIES);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }

    private User createUser(String email) {
        return User.builder()
                .password("password")
                .nickname("nickname")
                .email(email)
                .sns("LOCAL")
                .userParties(null)
                .build();
    }
}
