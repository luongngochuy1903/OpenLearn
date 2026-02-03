package com.example.online.helper;

import java.security.SecureRandom;
import java.util.Base64;

public class Base64Generator {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static String generateCode() {
        byte[] bytes = new byte[32]; // 256-bit
        SECURE_RANDOM.nextBytes(bytes);

        // URL-safe, không có "=" padding
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }
}
