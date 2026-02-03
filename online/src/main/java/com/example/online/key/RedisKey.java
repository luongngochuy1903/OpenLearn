package com.example.online.key;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class RedisKey {

    public static String jwtBlacklist(String jti) {
        return "JWT_BLACKLIST:" + jti;
    }

    public static String userValidAfter(Long userId) {
        return "USER_VALID_AFTER:" + userId;
    }
}
