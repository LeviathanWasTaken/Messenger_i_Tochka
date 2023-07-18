package com.leviathan.messenger_i_tochka.service;

import com.leviathan.messenger_i_tochka.dto.ChannelCreationRequest;
import com.leviathan.messenger_i_tochka.dto.ChannelsResponse;
import com.leviathan.messenger_i_tochka.dto.MessageDto;
import com.leviathan.messenger_i_tochka.entity.*;
import com.leviathan.messenger_i_tochka.exception.ChannelAlreadyExistException;
import com.leviathan.messenger_i_tochka.model.ChannelPermissionNames;
import com.leviathan.messenger_i_tochka.repository.ChannelMemberRepo;
import com.leviathan.messenger_i_tochka.repository.ChannelMessageRepo;
import com.leviathan.messenger_i_tochka.repository.ChannelRepo;
import com.leviathan.messenger_i_tochka.repository.ChannelRoleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChannelService {
    private final ChannelRepo channelRepo;
    private final ChannelMessageRepo channelMessageRepo;
    private final UserService userService;
    private final ChannelMemberRepo channelMemberRepo;
    private final ChannelRoleRepo channelRoleRepo;

    public List<ChannelsResponse> getAllChannels() {
        return channelRepo.findAll().stream().map(channel -> ChannelsResponse.builder()
                .tag(channel.getTag())
                .name(channel.getName())
                .build()).collect(Collectors.toList());
    }

    public List<ChannelsResponse> getAllChannelsForUser(String username) {
        User user = userService.findByUsername(username).get();
        List<Channel> channels = user.getMemberList().stream().map(ChannelMember::getChannel).toList();
        return channels.stream().map(channel -> ChannelsResponse.builder()
                .tag(channel.getTag())
                .name(channel.getName())
                .lastMessage(getMessages(channel, PageRequest.of(0, 1)).stream().findFirst().orElse(null))
                .build()).collect(Collectors.toList());
    }

    public List<MessageDto> getMessages(Channel channel, Pageable pageable) {
        return channelMessageRepo.findAllByChannelOrderByTimestampDesc(channel, pageable).stream().map(
                channelMessage -> MessageDto.builder()
                        .messageId(channelMessage.getId())
                        .content(channelMessage.getContent())
                        .timestamp(channelMessage.getTimestamp())
                        .authorUsername(channelMessage.getAuthor().getUser().getUsername())
                        .build()
        ).collect(Collectors.toList());
    }

    /**
     * Creates channel, default roles: Admin, Member - and sets Admin role to the creator of the channel
     * @param creationRequest - must contain: tag, name and creator's username
     */
    @Transactional
    public void createChannel(ChannelCreationRequest creationRequest) throws ChannelAlreadyExistException {
        if (channelRepo.findById(creationRequest.getTag()).isPresent())
            throw new ChannelAlreadyExistException("Channel with tag " + creationRequest.getTag() + " already exist");
        User user = userService.findByUsername(creationRequest.getCreatorUsername()).get();
        Channel channel = Channel.builder()
                .tag(creationRequest.getTag())
                .name(creationRequest.getName())
                .messageHistory(Collections.emptyList())
                .build();
        channel = channelRepo.save(channel);
        ChannelMember creator = ChannelMember.builder()
                .user(user)
                .channel(channel)
                .roles(List.of(createChannelRole("Admin", channel, List.of(
                        new ChannelPermission(ChannelPermissionNames.SEND_MESSAGES.name(), true),
                        new ChannelPermission(ChannelPermissionNames.EDIT_CHANNEL.name(), true),
                        new ChannelPermission(ChannelPermissionNames.MANAGE_MEMBERS.name(), true)
                ))))
                .build();
        channelMemberRepo.save(creator);
        createChannelRole("Member", channel, List.of(
                new ChannelPermission(ChannelPermissionNames.SEND_MESSAGES.name(), true),
                new ChannelPermission(ChannelPermissionNames.EDIT_CHANNEL.name(), false),
                new ChannelPermission(ChannelPermissionNames.MANAGE_MEMBERS.name(), false)
        ));
    }

    /**
     * Creates new role and saves it in db
     * @param roleName - Name of the role
     * @param channel - Role channel
     * @param permissions - List of permissions
     * @return - entity from db
     */
    private ChannelRole createChannelRole(String roleName, Channel channel, List<ChannelPermission> permissions) {
        ChannelRole newRole = ChannelRole.builder()
                .name(roleName)
                .channel(channel)
                .permissions(permissions)
                .build();
        return channelRoleRepo.save(newRole);
    }
}
