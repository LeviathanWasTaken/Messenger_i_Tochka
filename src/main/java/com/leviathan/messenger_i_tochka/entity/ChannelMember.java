package com.leviathan.messenger_i_tochka.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "channel_members")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChannelMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne
    private User user;
    @ManyToOne
    private Channel channel;
    @ManyToMany
    @JoinTable(
            name = "channel_members_channel_roles",
            joinColumns = @JoinColumn(name = "channel_members_id"),
            inverseJoinColumns = @JoinColumn(name = "channel_roles_id")
    )
    private Set<ChannelRole> roles;
}
