package com.leviathan.messenger_i_tochka.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "channel_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChannelMessage {
    @Id
    @Column(name = "id")
    UUID id;
    @ManyToOne
    ChannelMember author;
    @ManyToOne
    Channel channel;
    @Column(name = "content", nullable = false)
    String content;
    @Column(name = "timestamp")
    Date timestamp;
}
