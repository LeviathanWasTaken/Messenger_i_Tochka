package com.leviathan.messenger_i_tochka.service;

import com.leviathan.messenger_i_tochka.dto.ChatResponse;
import com.leviathan.messenger_i_tochka.dto.ChatShortResponse;
import com.leviathan.messenger_i_tochka.dto.MessageDto;
import com.leviathan.messenger_i_tochka.entity.Chat;
import com.leviathan.messenger_i_tochka.entity.ChatMessage;
import com.leviathan.messenger_i_tochka.entity.User;
import com.leviathan.messenger_i_tochka.exception.NotFoundException;
import com.leviathan.messenger_i_tochka.repository.ChatMessageRepo;
import com.leviathan.messenger_i_tochka.repository.ChatRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatMessageRepo chatMessageRepo;
    private final ChatRepo chatRepo;
    private final UserService userService;

    public ChatMessage findMessageById(UUID messageId) throws NotFoundException {
        return chatMessageRepo.findById(messageId).orElseThrow(
                () -> new NotFoundException("Message with id " + messageId + " not found in database")
        );
    }

    public Chat findChatById(UUID chatId) throws NotFoundException {
        return chatRepo.findById(chatId).orElseThrow(
                () -> new NotFoundException("Chat with id " + chatId + " not found in database")
        );
    }

    @Transactional
    public UUID createNewChat(List<String> membersUsernames) throws NotFoundException {
        List<User> members = new ArrayList<>();
        for (String username : membersUsernames) {
            members.add(userService.findByUsername(username).orElseThrow(
                    () -> new NotFoundException("User with username " + username + " does not exist")
            ));
        }

        UUID chatId = UUID.randomUUID();

        Chat chat = Chat.builder()
                .id(chatId)
                .members(members)
                .messageHistory(Collections.emptyList())
                .build();
        chatRepo.save(chat);
        createNewMessage(MessageDto.builder()
                .authorUsername("System")
                .content("Chat was created")
                .build(), chatId);
        return chatId;
    }

    public List<ChatShortResponse> getAllChatsForUser(String username) throws NotFoundException {
        User user = userService.findByUsername(username).orElseThrow(
                () -> new NotFoundException("User with username does not exist")
        );
        List<Chat> chats = chatRepo.findAllByMembersContains(user);
        return chats.stream().map(chat -> ChatShortResponse.builder()
                .chatId(chat.getId())
                .membersUsernames(
                        chat.getMembers().stream().map(User::getUsername).collect(Collectors.toList())
                )
                .lastMessage(getChatMessages(chat, PageRequest.of(0, 1)).stream().findFirst().orElse(null))
                .build()
        ).collect(Collectors.toList());
    }

    public List<MessageDto> getChatMessages(Chat chat, Pageable pageable) {
        List<ChatMessage> messages = chatMessageRepo.findAllByChatOrderByTimestampDesc(chat, pageable);
        return messages.stream().map(message -> MessageDto.builder()
                .messageId(message.getId())
                .authorUsername(message.getAuthor().getUsername())
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .build()
        ).collect(Collectors.toList());
    }

    @Transactional
    public void createNewMessage(MessageDto messageDto, UUID chatId) throws NotFoundException {
        Chat chat = findChatById(chatId);
        User author = userService.findByUsername(messageDto.getAuthorUsername()).get();
        ChatMessage chatMessage = ChatMessage.builder()
                .id(UUID.randomUUID())
                .chat(chat)
                .author(author)
                .content(messageDto.getContent())
                .timestamp(new Date())
                .build();
        chatMessageRepo.save(chatMessage);
    }

    public ChatResponse getChat(UUID chatId, Pageable pageable) throws NotFoundException {
        Chat chat = findChatById(chatId);
        return ChatResponse.builder()
                .chatId(chat.getId())
                .membersUsernames(
                        chat.getMembers().stream().map(User::getUsername).collect(Collectors.toList())
                )
                .messages(getChatMessages(chat, pageable))
                .build();
    }

    public boolean isUserParticipateInChat(UUID chatId, String username) throws NotFoundException {
        User user = userService.findByUsername(username).get();
        Chat chat = findChatById(chatId);
        return chat.getMembers().contains(user);
    }

    public List<MessageDto> getNewMessages(UUID chatId, UUID lastMessageId) throws NotFoundException {
        Chat chat = findChatById(chatId);
        ChatMessage message = findMessageById(lastMessageId);

        return chatMessageRepo.findAllByChatAndTimestampGreaterThanOrderByTimestampDesc(chat, message.getTimestamp())
                .stream().map(
                        messageFromDb -> MessageDto.builder()
                                .messageId(messageFromDb.getId())
                                .authorUsername(messageFromDb.getAuthor().getUsername())
                                .content(messageFromDb.getContent())
                                .timestamp(messageFromDb.getTimestamp())
                                .build()
                ).collect(Collectors.toList());
    }
}
