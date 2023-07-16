package com.leviathan.messenger_i_tochka.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "channel_roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @ManyToOne
    private Channel channel;
    @ManyToMany
    @JoinTable(
            name = "channel_roles_channel_permissions",
            joinColumns = @JoinColumn(name = "channel_roles_id"),
            inverseJoinColumns = @JoinColumn(name = "channel_permissions_id")
    )
    private Set<ChannelPermission> permissions;
}
