package com.leviathan.messenger_i_tochka.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChannelRequest {
    String tag;
    String username;
    Pageable pageable;
}
