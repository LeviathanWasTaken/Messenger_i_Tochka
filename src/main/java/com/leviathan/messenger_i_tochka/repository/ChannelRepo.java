package com.leviathan.messenger_i_tochka.repository;

import com.leviathan.messenger_i_tochka.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelRepo extends JpaRepository<Channel, String> {
    List<Channel> findAllByIsPublic(Boolean isPublic);
}
