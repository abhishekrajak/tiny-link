package com.abhishek.github.tinylink.util;

import java.security.SecureRandom;

import static com.abhishek.github.tinylink.constant.StringConstants.NumericConstant.INT_ZERO;

public final class DemoUserGenerator {
    private DemoUserGenerator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    private static final int length = 6;

    private static final String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String generateDemoUserId() {
        StringBuilder demoUserId = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = INT_ZERO; i < length; i++) {
            demoUserId.append(allowedChars.charAt(random.nextInt(allowedChars.length())));
        }
        return demoUserId.toString();
    }


}