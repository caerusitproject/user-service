package com.caerus.userservice.service;

import com.caerus.userservice.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {

    private final String TOKEN_PREFIX = "reset-token:";

    private final RedisTemplate<String, String> redisTemplate;

    public void saveToken(Long userId, String token, Duration ttl){
        String key = TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(key, String.valueOf(userId), ttl);
    }

    public long validateToken(String token){
        String key = TOKEN_PREFIX + token;
        String userId = redisTemplate.opsForValue().get(key);
        if (userId == null){
           throw new BadRequestException("Invalid or expired token");
        }
        return Long.valueOf(userId);
    }

    public void invalidateToken(String token){
        String key = TOKEN_PREFIX + token;
        redisTemplate.delete(key);
    }
}
