package com.leviathan.messenger_i_tochka.controller;

import com.leviathan.messenger_i_tochka.dto.ChannelCreationRequest;
import com.leviathan.messenger_i_tochka.dto.ChannelsResponse;
import com.leviathan.messenger_i_tochka.dto.MessageDto;
import com.leviathan.messenger_i_tochka.exception.AppError;
import com.leviathan.messenger_i_tochka.exception.ChannelAlreadyExistException;
import com.leviathan.messenger_i_tochka.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    @GetMapping("/api/channel/all")
    public ResponseEntity<?> getChannelsList() {
        return ResponseEntity.ok(channelService.getAllChannels());
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
        } catch (ChannelAlreadyExistException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }
}
