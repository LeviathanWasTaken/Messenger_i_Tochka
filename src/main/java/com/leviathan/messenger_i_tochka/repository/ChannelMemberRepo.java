package com.leviathan.messenger_i_tochka.repository;

import com.leviathan.messenger_i_tochka.entity.ChannelMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelMemberRepo extends JpaRepository<ChannelMember, Long> {
}
