package com.leviathan.messenger_i_tochka.controller;

import com.leviathan.messenger_i_tochka.dto.ChatAddUsersRequest;
import com.leviathan.messenger_i_tochka.dto.ChatCreationRequest;
import com.leviathan.messenger_i_tochka.dto.ChatCreationResponse;
import com.leviathan.messenger_i_tochka.dto.MessageDto;
import com.leviathan.messenger_i_tochka.exception.AppError;
import com.leviathan.messenger_i_tochka.exception.NotFoundException;
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
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @PostMapping("/api/chats")
    public ResponseEntity<?> startChatWithUsers(@RequestBody ChatCreationRequest chatCreationRequest, Principal principal) {
        List<String> membersUsernames = new ArrayList<>(chatCreationRequest.getMembersUsernames());
        membersUsernames.add(principal.getName());
        UUID createdChatUUID;
        try {
            createdChatUUID = chatService.createNewChat(membersUsernames);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ChatCreationResponse(createdChatUUID));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @PostMapping("/api/chats/{chatId}")
    public ResponseEntity<?> sendMessage(@RequestBody MessageDto message, @PathVariable UUID chatId, Principal principal) {
        message.setAuthorUsername(principal.getName());
        try{
            chatService.createNewMessage(message, chatId);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/api/chats/{chatId}")
    public ResponseEntity<?> getChat(@PathVariable UUID chatId, @RequestParam int page, @RequestParam int messagesAmount, Principal principal) {
        try {
            if (!chatService.isUserParticipateInChat(chatId, principal.getName()))
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            return ResponseEntity.ok(chatService.getChat(chatId, PageRequest.of(page, messagesAmount)));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @GetMapping("/api/chats/{chatId}/refresh")
    public ResponseEntity<?> refreshChat(@PathVariable UUID chatId, @RequestParam UUID lastMessageId, Principal principal) {
        try {
            if (!chatService.isUserParticipateInChat(chatId, principal.getName()))
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            return ResponseEntity.ok(chatService.getNewMessages(chatId, lastMessageId));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @PutMapping("/api/chats/{chatId}/add")
    public ResponseEntity<?> addUsersToChat(@PathVariable UUID chatId, @RequestBody ChatAddUsersRequest addUsersRequest, Principal principal) {
        addUsersRequest.setChatId(chatId);
        try {
            if (chatService.isUserParticipateInChat(chatId, principal.getName())) {
                chatService.addUsersToChat(addUsersRequest);
                return ResponseEntity.ok().build();
            } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @GetMapping("/api/chats/{chatId}/leave")
    public ResponseEntity<?> leaveChat(@PathVariable UUID chatId, Principal principal) {
        try {
            chatService.removeUserFromChat(principal.getName(), chatId);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

}
