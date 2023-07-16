package com.leviathan.messenger_i_tochka.controller;

import com.leviathan.messenger_i_tochka.dto.ChatCreationRequest;
import com.leviathan.messenger_i_tochka.dto.ChatCreationResponse;
import com.leviathan.messenger_i_tochka.dto.MessageDto;
import com.leviathan.messenger_i_tochka.exception.ChatAlreadyExistException;
import com.leviathan.messenger_i_tochka.exception.ChatNotFoundException;
import com.leviathan.messenger_i_tochka.exception.MessageNotFoundException;
import com.leviathan.messenger_i_tochka.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/api/chats")
    public ResponseEntity<?> getAllChats(Principal principal) {
        try {
            return ResponseEntity.ok(chatService.getAllChatsForUser(principal.getName()));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/api/chats")
    public ResponseEntity<?> startChatWithUsers(@RequestBody ChatCreationRequest chatCreationRequest, Principal principal) {
        List<String> membersUsernames = new ArrayList<>(chatCreationRequest.getMembersUsernames());
        membersUsernames.add(principal.getName());
        UUID createdChatUUID;
        try {
            createdChatUUID = chatService.createNewChat(membersUsernames);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ChatAlreadyExistException e) {
            return ResponseEntity.ok(new ChatCreationResponse(UUID.fromString(e.getMessage())));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new ChatCreationResponse(createdChatUUID));
    }

    @PostMapping("/api/chats/{chatId}")
    public ResponseEntity<?> sendMessage(@RequestBody MessageDto message, @PathVariable UUID chatId, Principal principal) {
        message.setAuthorUsername(principal.getName());
        try{
            chatService.createNewMessage(message, chatId);
        } catch (ChatNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/api/chats/{chatId}")
    public ResponseEntity<?> getChat(@PathVariable UUID chatId, @RequestParam int page, @RequestParam int messagesAmount, Principal principal) {
        try {
            if (!chatService.isUserParticipateInChat(chatId, principal.getName()))
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            return ResponseEntity.ok(chatService.getChat(chatId, PageRequest.of(page, messagesAmount)));
        } catch (ChatNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/api/chats/{chatId}/refresh")
    public ResponseEntity<?> refreshChat(@PathVariable UUID chatId, @RequestParam UUID lastMessageId, Principal principal) {
        try {
            if (!chatService.isUserParticipateInChat(chatId, principal.getName()))
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            return ResponseEntity.ok(chatService.getNewMessages(chatId, lastMessageId));
        } catch (ChatNotFoundException | MessageNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
