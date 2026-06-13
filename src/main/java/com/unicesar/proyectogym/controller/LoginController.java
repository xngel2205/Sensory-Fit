
package com.unicesar.proyectogym.controller;

import com.unicesar.proyectogym.service.LoginService;


public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    public boolean login(String user, char[] password) {
        return loginService.authenticate(user, password);
    }
}
