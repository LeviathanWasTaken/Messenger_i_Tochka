package com.leviathan.messenger_i_tochka.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "channels")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Channel {
    @Id
    @Column(name = "tag", nullable = false, unique = true)
    String tag;
    @Column(name = "name", nullable = false)
    String name;
    @OneToMany(mappedBy = "channel")
    List<ChannelRole> channelRoles;
    @OneToMany(mappedBy = "channel")
    List<ChannelMember> members;
    @OneToMany(mappedBy = "channel")
    List<ChannelMessage> messageHistory;
}
