package com.prazk.myshortlink.admin.util;

import java.security.SecureRandom;
import java.util.Set;
public class GidGenerator {
    private static final String CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String generateUniqueID(Set<String> existingIds, int length) {
        SecureRandom random = new SecureRandom();
        StringBuffer sb = new StringBuffer(length);
        boolean unique = false;

        while (!unique) {
            for (int i = 0; i < length; i++) {
                int randomIndex = random.nextInt(CHARACTERS.length());
                sb.append(CHARACTERS.charAt(randomIndex));
            }
            String generatedID = sb.toString();

            if (!existingIds.contains(generatedID)) {
                unique = true;
            } else {
                sb.setLength(0);
            }
        }

        return sb.toString();
    }
}

