package com.leviathan.messenger_i_tochka.repository;

import com.leviathan.messenger_i_tochka.entity.Chat;
import com.leviathan.messenger_i_tochka.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatRepo extends JpaRepository<Chat, UUID> {
    List<Chat> findAllByMembersContains(User member);
}
