package com.leviathan.messenger_i_tochka.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "channel_messages")
@Data
@NoArgsConstructor
public class ChannelMessage {
    @Id
    @Column(name = "id")
    private UUID id;
    @ManyToOne
    private ChannelMember author;
    @ManyToOne
    private Channel channel;
    @Column(name = "content", nullable = false)
    private String content;
    @Column(name = "timestamp")
    private Date timestamp;
}
