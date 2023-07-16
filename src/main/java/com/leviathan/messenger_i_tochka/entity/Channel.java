package com.leviathan.messenger_i_tochka.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "channels")
@Data
@NoArgsConstructor
public class Channel {
    @Id
    @Column(name = "tag")
    private String tag;
    @Column(name = "name")
    private String name;
    @OneToMany(mappedBy = "channel")
    private Set<ChannelMember> members;
    @OneToMany(mappedBy = "channel")
    private Set<ChannelMessage> messageHistory;
    @OneToMany(mappedBy = "channel")
    private Set<ChannelRole> channelRoles;
}
