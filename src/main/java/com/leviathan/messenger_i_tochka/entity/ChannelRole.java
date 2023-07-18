package com.leviathan.messenger_i_tochka.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "channel_roles")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChannelRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @Column(name = "name")
    String name;
    @ManyToOne
    Channel channel;
    @ManyToMany
    List<ChannelMember> members;
    @ElementCollection
    @CollectionTable(name = "channel_roles_permissions", joinColumns = @JoinColumn(name = "channel_roles_id"))
    List<ChannelPermission> permissions;

}
