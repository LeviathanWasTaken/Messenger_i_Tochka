package com.leviathan.messenger_i_tochka.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "chats")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Chat {
    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToMany
    @JoinTable(
            name = "chats_users",
            joinColumns = @JoinColumn(name = "chats_id"),
            inverseJoinColumns = @JoinColumn(name = "users_username")
    )
    private List<User> members;

    @OneToMany(mappedBy = "chat")
    private List<ChatMessage> messageHistory;
}
