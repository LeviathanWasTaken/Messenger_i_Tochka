package com.leviathan.messenger_i_tochka.repository;

import com.leviathan.messenger_i_tochka.entity.Channel;
import com.leviathan.messenger_i_tochka.entity.ChannelRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChannelRoleRepo extends JpaRepository<ChannelRole, Long> {
}
