package com.partyquest.backend.config;

import com.partyquest.backend.config.jwt.TokenProvider;
import com.partyquest.backend.config.redis.RedisDao;
import com.partyquest.backend.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TokenProviderTest {

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    RedisDao redisDao;

    @Test
    void tokenTest() {
        User user = new User(0,"test@test.com","test","1234","1234",null,null);
        String[] tokens = tokenProvider.createToken(user).split("::");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization","Bearer "+tokens[0]);
        request.addHeader("RefreshToken",tokens[1]);

        MockHttpServletResponse response = new MockHttpServletResponse();

        Long userId = tokenProvider.getUserId(request, response);
        assertAll(
                ()->assertEquals(tokens[1],redisDao.getValue("0")),
                ()->assertEquals(userId,0)
        );
    }
}