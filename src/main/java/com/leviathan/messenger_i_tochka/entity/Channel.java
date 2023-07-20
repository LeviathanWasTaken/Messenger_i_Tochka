package com.leviathan.messenger_i_tochka.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;

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
    @Column(name = "public", columnDefinition = "boolean default false")
    Boolean isPublic;
    @OneToOne
    @JoinTable(
            name = "channels_default_roles",
            joinColumns = @JoinColumn(name = "channels_tag"),
            inverseJoinColumns = @JoinColumn(name = "channel_roles_id")
    )
    ChannelRole defaultRole;
    @OneToMany(mappedBy = "channel")
    List<ChannelRole> channelRoles;
    @OneToMany(mappedBy = "channel")
    List<ChannelMember> members;
    @OneToMany(mappedBy = "channel")
    List<ChannelMessage> messageHistory;
    @OneToMany(mappedBy = "channel")
    List<ChannelInvite> invites;
}
