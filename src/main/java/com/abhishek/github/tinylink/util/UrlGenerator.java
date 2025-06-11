package com.abhishek.github.tinylink.util;

public final class UrlGenerator {
    private UrlGenerator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static String generateShortCode(int length, String allowedChars) {
        StringBuilder shortCode = new StringBuilder();
        for (int i = 0; i < length; i++) {
            shortCode.append(allowedChars.charAt((int) (Math.random() * allowedChars.length())));
        }
        return shortCode.toString();
    }


}