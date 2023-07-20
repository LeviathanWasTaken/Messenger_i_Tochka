package com.leviathan.messenger_i_tochka.repository;

import com.leviathan.messenger_i_tochka.entity.ChannelInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChannelInviteRepo extends JpaRepository<ChannelInvite, UUID> {
}
