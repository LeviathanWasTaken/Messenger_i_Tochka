package com.leviathan.messenger_i_tochka.repository;

import com.leviathan.messenger_i_tochka.entity.Channel;
import com.leviathan.messenger_i_tochka.entity.ChannelMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChannelMessageRepo extends JpaRepository<ChannelMessage, UUID> {
    List<ChannelMessage> findAllByChannelOrderByTimestampDesc(Channel channel, Pageable pageable);
}
