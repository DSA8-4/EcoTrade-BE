package com.example.board.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    // 비밀번호를 해시화하는 메서드
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // 비밀번호와 해시된 비밀번호를 비교하는 메서드
    public static boolean validatePassword(String rawPassword, String hashedPassword) {
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }
}
