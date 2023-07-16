package com.leviathan.messenger_i_tochka.exception;

import java.util.UUID;


public class ChatAlreadyExistException extends Exception{
    public ChatAlreadyExistException(UUID existingChatId) {
        super(existingChatId.toString());
    }
}
