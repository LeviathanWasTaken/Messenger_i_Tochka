package com.leviathan.messenger_i_tochka.controller;

import com.leviathan.messenger_i_tochka.dto.UsersResponse;
import com.leviathan.messenger_i_tochka.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/users")
    public ResponseEntity<?> getAllUsers() {
        List<UsersResponse> response = userService.findAll();
        return ResponseEntity.ok(response);
    }

}
