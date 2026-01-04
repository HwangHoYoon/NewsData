package com.news.newsdata.token.service;

import com.news.newsdata.common.service.JwtProviderService;
import com.news.newsdata.token.entity.Token;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
@Slf4j
public class TokenService {

    private static final String HASH_KEY = "token";

    private final RedisTemplate<String, Token> redisTemplate;

    public void save(Token token) {
        ValueOperations<String, Token> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(HASH_KEY + ":" + token.getRefreshToken(), token);
        redisTemplate.expire(HASH_KEY + ":" + token.getRefreshToken(), JwtProviderService.REFRESH_TIME, TimeUnit.SECONDS);
    }

    public Token findById(String token) {
        ValueOperations<String, Token> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(HASH_KEY + ":" + token);
    }
    public void saveHeader(String id, Token token) {
        ValueOperations<String, Token> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(id, token);
        redisTemplate.expire(id, 600, TimeUnit.SECONDS);
    }

    public Token findByHeaderId(String id) {
        ValueOperations<String, Token> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(id);
    }

    public void update(Token token) {
        save(token); // Hash에 이미 존재하면 덮어쓰기 역할을 합니다.
    }

    public void deleteById(String token) {
        redisTemplate.delete(HASH_KEY + ":" + token);
    }
}
