package com.abhishek.github.tinylink.util;

import static com.abhishek.github.tinylink.constant.StringConstants.NumericConstant.INT_ZERO;

public final class UrlGenerator {
    private UrlGenerator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static String generateShortCode(int length, String allowedChars) {
        StringBuilder shortCode = new StringBuilder();
        for (int i = INT_ZERO; i < length; i++) {
            shortCode.append(allowedChars.charAt((int) (Math.random() * allowedChars.length())));
        }
        return shortCode.toString();
    }


}