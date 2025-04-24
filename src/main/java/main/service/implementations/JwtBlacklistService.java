package main.service.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class JwtBlacklistService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String BLACKLIST_PREFIX = "blacklisted_token";

    public void addToBlacklist(String token) {
        stringRedisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "blacklisted", Duration.ofMillis(1000 * 60 * 60));
    }

    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }

}
