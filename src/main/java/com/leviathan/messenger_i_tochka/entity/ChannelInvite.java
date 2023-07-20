package com.leviathan.messenger_i_tochka.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "channel_invites")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChannelInvite {
    @Id
    @Column(name = "invite_id")
    UUID id;
    @ManyToOne
    Channel channel;
    @Column(name = "expires")
    Date expiresAt;
    @ManyToOne
    ChannelMember creator;
}
