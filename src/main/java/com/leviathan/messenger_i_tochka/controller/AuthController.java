package com.leviathan.messenger_i_tochka.controller;

import com.leviathan.messenger_i_tochka.dto.JwtRequest;
import com.leviathan.messenger_i_tochka.dto.JwtResponse;
import com.leviathan.messenger_i_tochka.dto.RegistrationUserDto;
import com.leviathan.messenger_i_tochka.exception.AlreadyExistException;
import com.leviathan.messenger_i_tochka.exception.AppError;
import com.leviathan.messenger_i_tochka.service.AuthService;
import com.leviathan.messenger_i_tochka.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/api/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        String token;
        try {
            token = authService.createAuthToken(authRequest);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Wrong login or password"), HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping("/api/auth/registration")
    public ResponseEntity<?> createNewUser(@RequestBody RegistrationUserDto registrationUserDto) {
        try {
            userService.createNewUser(registrationUserDto);
        } catch (AlreadyExistException e) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/api/auth/logout")
    public ResponseEntity<?> blacklistTokens(Principal principal) {
        authService.blacklistTokens(principal.getName());
        return ResponseEntity.ok().build();
    }

}

































