package com.leviathan.messenger_i_tochka.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "channel_members")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChannelMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @ManyToOne
    User user;
    @ManyToOne
    Channel channel;
    @Column(name = "active", columnDefinition = "boolean default true")
    Boolean isActive;
    @ManyToMany
    @JoinTable(
            name = "channel_members_roles",
            joinColumns = @JoinColumn(name = "channel_members_id"),
            inverseJoinColumns = @JoinColumn(name = "channel_roles_id")
    )
    List<ChannelRole> roles;
    @OneToMany(mappedBy = "author")
    List<ChannelMessage> messages;
    @OneToMany(mappedBy = "creator")
    List<ChannelInvite> invites;
}
