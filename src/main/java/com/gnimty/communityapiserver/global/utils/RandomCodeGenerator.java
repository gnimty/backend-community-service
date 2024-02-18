package com.gnimty.communityapiserver.global.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RandomCodeGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateCodeByLength(int length) {
        StringBuilder res = new StringBuilder();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int charactersLength = characters.length();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(charactersLength);
            res.append(characters.charAt(randomIndex));
        }
        return res.toString();
    }

}
