package com.partyquest.backend.config.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisDao {
    private final RedisTemplate<String,String> redisTemplate;

    @Autowired
    public RedisDao(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setValue(String key, String value, Duration expiration) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(key,value,expiration);
    }
    public String getValue(String key) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        if(values.get(key) != null) {
            return values.get(key);
        } else {
            return null;
        }
    }
    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }
}
