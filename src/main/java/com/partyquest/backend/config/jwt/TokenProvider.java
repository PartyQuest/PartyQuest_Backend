package com.partyquest.backend.config.jwt;

import com.partyquest.backend.config.exception.ErrorCode;
import com.partyquest.backend.config.redis.RedisDao;
import com.partyquest.backend.domain.entity.User;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
@Slf4j
public class TokenProvider {

    @Value("${spring.jwt.secret}")
    private String jwtToken;

    private final RedisDao redisDao;

    @Autowired
    public TokenProvider(RedisDao redisDao) {
        this.redisDao = redisDao;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser().setSigningKey(jwtToken).parseClaimsJws(token).getBody();
    }

    private Claims validateToken(String token) {
        Claims claims = null;
        try {
            claims = parseClaims(token);
        } catch (MalformedJwtException e) {
            log.error("MalformedJwtException",e);
            throw new JwtException(ErrorCode.JWT_TOKEN_MALFORMED.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("ExpiredJwtException",e);
            throw new JwtException(ErrorCode.JWT_TOKEN_EXPIRED.getMessage());
        } catch (SignatureException e) {
            log.error("SignatureException",e);
            throw new JwtException(ErrorCode.JWT_TOKEN_WRONG_TYPE.getMessage());
        }
        return claims;
    }

    public Long getUserId(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("Authorization").substring(7);
        Claims claims = validateToken(token);
        return Long.parseLong(claims.getSubject());
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
        String refreshToken = tokenBuilder(30,ChronoUnit.DAYS,claims,now);
        redisDao.setValue(Long.toString(user.getId()),refreshToken, Duration.ofSeconds(30));
        return accessToken+"::"+refreshToken;
    }
}
