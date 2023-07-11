package com.leviathan.messenger_i_tochka.exception;

public class JwtBlacklistedException extends Exception {
    public JwtBlacklistedException(String message) {
        super(message);
    }
}
