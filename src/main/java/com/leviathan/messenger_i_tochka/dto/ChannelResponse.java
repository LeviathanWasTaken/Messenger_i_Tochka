package com.leviathan.messenger_i_tochka.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChannelResponse {
    String tag;
    String name;
    List<String> memberRoles;
    HashMap<String, Boolean> memberPermissions;
    List<MessageDto> messages;
}
