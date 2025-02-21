package me.carmelo.theforums.service.auth;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class SessionService implements ISessionService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String SESSION_PREFIX = "user_session:";

    public SessionService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String getSessionKey(String username) {
        return SESSION_PREFIX + username;
    }

    @Override
    public boolean isSessionActive(String username) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(getSessionKey(username)));
    }

    @Override
    public void createSession(String username) {
        redisTemplate.opsForValue().set(getSessionKey(username), "active");
    }

    @Override
    public boolean invalidateSession(String username) {
        return Boolean.TRUE.equals(redisTemplate.delete(getSessionKey(username)));
    }
}
