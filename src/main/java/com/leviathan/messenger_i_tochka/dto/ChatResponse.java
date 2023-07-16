package com.leviathan.messenger_i_tochka.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatResponse {
    UUID chatId;
    List<String> membersUsernames;
    List<MessageDto> messages;
}
