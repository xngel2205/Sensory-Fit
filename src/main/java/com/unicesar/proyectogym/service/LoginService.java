package com.unicesar.proyectogym.service;

import java.util.Arrays;

public class LoginService {

    private static final String ADMIN_USER = "admin";
    private static final char[] ADMIN_PASSWORD = "2026*".toCharArray();

  
    public boolean authenticate(String user, char[] password) {
        if (user == null || password == null) {
            return false;
        }
        boolean userOk = ADMIN_USER.equals(user.trim());
        boolean passOk = Arrays.equals(ADMIN_PASSWORD, password);
        return userOk && passOk;
    }
}
