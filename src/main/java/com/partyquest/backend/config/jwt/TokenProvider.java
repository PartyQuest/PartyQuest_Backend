package com.partyquest.backend.config.jwt;

import com.partyquest.backend.config.redis.RedisDao;
import com.partyquest.backend.domain.entity.User;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class TokenProvider {
    @Value("${spring.jwt.secret}")
    private String jwtToken;

    private final RedisDao redisDao;

    @Autowired
    public TokenProvider(RedisDao redisDao) {
        this.redisDao = redisDao;
    }

    private Boolean checkExpiration(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(jwtToken).parseClaimsJws(token);
            return claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException eje) {
            return false;
        }
    }
    private boolean existsRefreshToken(String refreshToken,long userId) {
        if(redisDao.getValue(Long.toString(userId)) != null) {
            return true;
        } else {
            return false;
        }
    }
    private Long getRefreshToken(HttpServletResponse response, String refreshToken) {
        long userId = Long.parseLong(Jwts.parser().setSigningKey(jwtToken).parseClaimsJws(refreshToken).getBody().getSubject());
        if(checkExpiration(refreshToken) && existsRefreshToken(refreshToken,userId)) {
            Claims claims = Jwts.claims().setSubject(Long.toString(userId));
            String newAccessToken = tokenBuilder(1,ChronoUnit.HOURS,claims,new Date());
            response.setHeader("Authorization","Bearer "+newAccessToken);
            return userId;
        }
        return null;
    }
    private Long validate(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = request.getHeader("Authorization").substring(7);
        String refreshToken = request.getHeader("RefreshToken");

        if(checkExpiration(accessToken)) {
            return Long.parseLong(Jwts.parser().setSigningKey(jwtToken).parseClaimsJws(accessToken).getBody().getSubject());
        } else if (!checkExpiration(accessToken) && refreshToken != null) {
           return getRefreshToken(response,refreshToken);
        }
        return null;
    }
    public Long getUserId(HttpServletRequest request, HttpServletResponse response) {
        Jws<Claims> accessClaims;
        Jws<Claims> refreshClaims;

        JwtParser jwtParser = Jwts.parser().setSigningKey(jwtToken);

        Long userId = validate(request,response);
        if(userId != null) {
            return userId;
        } else {
            accessClaims = jwtParser.parseClaimsJws(request.getHeader("Authorization").substring(7));
            refreshClaims = jwtParser.parseClaimsJws(request.getHeader("RefreshToken"));
            return Long.parseLong(accessClaims.getBody().getSubject());
        }
    }


    private String tokenBuilder(int time, ChronoUnit chronoUnit, Claims claims, Date date) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(date)
                .setExpiration(Date.from(Instant.now().plus(time,chronoUnit)))
                .signWith(SignatureAlgorithm.HS512,jwtToken)
                .setIssuer("PartyQuest Server")
                .compact();
    }
    public String createToken(User user) {
        Claims claims = Jwts.claims().setSubject(Long.toString(user.getId()));
        Date now = new Date();

        String accessToken = tokenBuilder(1,ChronoUnit.HOURS,claims,now);
        String refreshToken = tokenBuilder(10,ChronoUnit.DAYS,claims,now);
        redisDao.setValue(Long.toString(user.getId()),refreshToken,Duration.ofDays(21));
        return accessToken+"::"+refreshToken;
    }
}
