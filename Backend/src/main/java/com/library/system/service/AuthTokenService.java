package com.library.system.service;

import com.library.system.common.AuthUser;
import com.library.system.entity.User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthTokenService {

    private static final long TOKEN_TTL_SECONDS = 60L * 60L * 24L;
    private final Map<String, TokenSession> tokenStore = new ConcurrentHashMap<>();

    public String issueToken(User user) {
        String token = UUID.randomUUID().toString().replace("-", "");
        long expireAt = Instant.now().getEpochSecond() + TOKEN_TTL_SECONDS;
        tokenStore.put(token, new TokenSession(new AuthUser(user.getId(), user.getUsername(), user.getRole()), expireAt));
        return token;
    }

    public AuthUser resolveUser(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        TokenSession session = tokenStore.get(token);
        if (session == null) {
            return null;
        }
        long now = Instant.now().getEpochSecond();
        if (now > session.expireAtEpochSecond) {
            tokenStore.remove(token);
            return null;
        }
        return session.user;
    }

    private record TokenSession(AuthUser user, long expireAtEpochSecond) {}
}
