package com.leviathan.messenger_i_tochka.repository;

import com.leviathan.messenger_i_tochka.entity.Chat;
import com.leviathan.messenger_i_tochka.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface ChatMessageRepo extends JpaRepository<ChatMessage, UUID> {
    List<ChatMessage> findAllByChatOrderByTimestampDesc(Chat chat, Pageable pageable);

    List<ChatMessage> findAllByChatAndTimestampGreaterThanOrderByTimestampDesc(Chat chat, Date timestamp);
}
