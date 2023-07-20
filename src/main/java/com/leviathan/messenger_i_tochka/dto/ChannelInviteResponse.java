package com.leviathan.messenger_i_tochka.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChannelInviteResponse {
    UUID inviteId;
    String channelTag;
    String channelName;
    Date expiresAt;
    String createdBy;
}
