package com.leviathan.messenger_i_tochka.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class main {
    @GetMapping("/unsecured")
    public String unsecured() {
        return "Unsecured endpoint";
    }

    @GetMapping("/secured")
    public String secured() {
        return "Secured endpoint";
    }

    @GetMapping("/user-info")
    public String userInfo(Principal principal) {
        return principal.getName();
    }
}
