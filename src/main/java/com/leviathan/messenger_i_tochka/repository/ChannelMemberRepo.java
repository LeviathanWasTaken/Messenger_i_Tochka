package com.leviathan.messenger_i_tochka.repository;

import com.leviathan.messenger_i_tochka.entity.Channel;
import com.leviathan.messenger_i_tochka.entity.ChannelMember;
import com.leviathan.messenger_i_tochka.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChannelMemberRepo extends JpaRepository<ChannelMember, Long> {
    Optional<ChannelMember> findByChannelAndUser(Channel channel, User user);

    Optional<ChannelMember> findByUserAndChannel(User user, Channel channel);

    List<ChannelMember> findByUser(User user);
}
