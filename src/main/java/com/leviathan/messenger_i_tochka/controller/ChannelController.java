package com.leviathan.messenger_i_tochka.controller;

import com.leviathan.messenger_i_tochka.dto.ChannelCreationRequest;
import com.leviathan.messenger_i_tochka.dto.ChannelRequest;
import com.leviathan.messenger_i_tochka.dto.MessageDto;
import com.leviathan.messenger_i_tochka.exception.*;
import com.leviathan.messenger_i_tochka.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    @GetMapping("/api/channel/public")
    public ResponseEntity<?> getPublicChannelsList() {
        return ResponseEntity.ok(channelService.getAllPublicChannels());
    }

    @GetMapping("/api/channel")
    public ResponseEntity<?> getChannelsListForUser(Principal principal) {
        return ResponseEntity.ok(channelService.getAllChannelsForUser(principal.getName()));
    }

    @PostMapping("/api/channel")
    public ResponseEntity<?> createChannel(Principal principal, @RequestBody ChannelCreationRequest creationRequest) {
        creationRequest.setCreatorUsername(principal.getName());
        try {
            channelService.createChannel(creationRequest);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (AlreadyExistException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @GetMapping("/api/channel/join/{tag}")
    public ResponseEntity<?> joinChannel(@PathVariable String tag, Principal principal) {
        try {
            channelService.joinChannel(principal.getName(), tag);
            return ResponseEntity.ok().build();
        } catch (UserAlreadyInChannelException | NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/api/channel/invite/{inviteId}")
    public ResponseEntity<?> getInviteInfo(@PathVariable UUID inviteId) {
        try {
            return ResponseEntity.ok(channelService.getInviteInfo(inviteId));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @GetMapping("/api/channel/join")
    public ResponseEntity<?> joinChannelByInvite(@RequestParam UUID inviteId, Principal principal) {
        try {
            channelService.joinChannelByInvite(principal.getName(), inviteId);
            return ResponseEntity.ok().build();
        } catch (NotFoundException | UserAlreadyInChannelException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (InviteExpiredException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AppError(HttpStatus.BAD_REQUEST.value(), "Invitation is expired"));
        }
    }

    @GetMapping("/api/channel/{tag}")
    public ResponseEntity<?> getChannel(@PathVariable String tag,
                                        @RequestParam(required = false, defaultValue = "0") int page,
                                        @RequestParam(required = false, defaultValue = "20") int messagesAmount,
                                        Principal principal) {
        try {
            return ResponseEntity.ok().body(channelService.getChannelInfo(ChannelRequest.builder()
                    .tag(tag)
                    .username(principal.getName())
                    .pageable(PageRequest.of(page, messagesAmount))
                    .build()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/api/channel/{tag}")
    public ResponseEntity<?> sendMessage(@PathVariable String tag, @RequestBody MessageDto message, Principal principal) {
        message.setAuthorUsername(principal.getName());
        try {
            if (channelService.isUserParticipateInChannel(principal.getName(), tag)) {
                channelService.createNewMessage(message, tag);
            } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/api/channel/{tag}/refresh")
    public ResponseEntity<?> refreshChannel(@PathVariable String tag, @RequestParam UUID lastMessageId, Principal principal) {
        try {
            if (channelService.isUserParticipateInChannel(principal.getName(), tag)) {
                return ResponseEntity.ok(channelService.getNewMessages(tag, lastMessageId));
            } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @GetMapping("/api/channel/{tag}/leave")
    public ResponseEntity<?> leaveChannel(@PathVariable String tag, Principal principal) {
        try {
            channelService.removeMemberFromChannel(principal.getName(), tag);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @GetMapping("/api/channel/{tag}/kick/{username}")
    public ResponseEntity<?> kickMember(@PathVariable String tag, @PathVariable String username, Principal principal) {
        try {
            channelService.kickMember(tag, principal.getName(), username);
            return ResponseEntity.ok().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @PostMapping("/api/channel/{tag}/createInvite")
    public ResponseEntity<?> createChannelInvite(@PathVariable String tag, Principal principal) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(channelService.createChannelInvite(tag, principal.getName()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
