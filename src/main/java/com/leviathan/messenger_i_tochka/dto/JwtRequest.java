package com.leviathan.messenger_i_tochka.dto;

import lombok.Data;

@Data
public class JwtRequest {
    private String username;
    private String password;
}
