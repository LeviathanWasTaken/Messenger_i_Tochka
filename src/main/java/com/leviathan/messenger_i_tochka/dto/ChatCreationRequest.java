package com.leviathan.messenger_i_tochka.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatCreationRequest {
    List<String> membersUsernames;
}
