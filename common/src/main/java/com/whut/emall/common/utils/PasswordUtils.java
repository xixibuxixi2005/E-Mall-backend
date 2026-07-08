package com.whut.emall.common.utils;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtils {
    public static String encryptPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(10));
    }

    public static boolean verifyPassword(String password, String encrypted) {
        return BCrypt.checkpw(password, encrypted);
    }
}
