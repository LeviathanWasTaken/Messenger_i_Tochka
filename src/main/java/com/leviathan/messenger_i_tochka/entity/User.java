package com.leviathan.messenger_i_tochka.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "users")
@NoArgsConstructor
public class User {
    @Id
    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "users_username"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<ApplicationRole> roles;

    @OneToMany(mappedBy = "author")
    private Set<ChatMessage> chatMessages;

    @CollectionTable(name = "users_jwt", joinColumns = @JoinColumn(name = "users_username"))
    @ElementCollection(targetClass = String.class, fetch = FetchType.LAZY)
    @Column(name = "token", nullable = false)
    private Set<String> tokens;

    @OneToMany(mappedBy = "user")
    private List<ChannelMember> memberList;

    public void addToken(String token) {
        this.tokens.add(token);
    }
}
