package com.leviathan.messenger_i_tochka.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat_messages")
public class ChatMessage {
    @Id
    @Column(name = "id")
    private UUID id;
    @ManyToOne
    private User author;
    @ManyToOne
    private Chat chat;

    @Column(name = "content", nullable = false)
    private String content;
    @Column(name = "timestamp")
    private Date timestamp;
}
