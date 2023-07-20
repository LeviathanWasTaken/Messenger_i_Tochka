package com.leviathan.messenger_i_tochka.service;

import com.leviathan.messenger_i_tochka.dto.*;
import com.leviathan.messenger_i_tochka.entity.*;
import com.leviathan.messenger_i_tochka.exception.*;
import com.leviathan.messenger_i_tochka.model.ChannelPermissionNames;
import com.leviathan.messenger_i_tochka.repository.*;
import com.leviathan.messenger_i_tochka.utils.SystemMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChannelService {
    private final ChannelRepo channelRepo;
    private final ChannelMessageRepo channelMessageRepo;
    private final UserService userService;
    private final ChannelMemberRepo channelMemberRepo;
    private final ChannelRoleRepo channelRoleRepo;
    private final ChannelInviteRepo channelInviteRepo;

    public Channel findByTag(String tag) throws NotFoundException {
        return channelRepo.findById(tag).orElseThrow(
                () -> new NotFoundException("Channel with tag " + tag + " doesn't exist")
        );
    }

    public ChannelInvite findInviteById(UUID inviteId) throws NotFoundException {
        return channelInviteRepo.findById(inviteId).orElseThrow(
                () -> new NotFoundException("Invite with id " + inviteId + " doesn't exist")
        );
    }

    public ChannelRole getDefaultChannelRole(Channel channel) {
        return channel.getDefaultRole();
    }

    @Transactional
    public void setDefaultChannelRole(Channel channel, ChannelRole defaultRole) {
        channel.setDefaultRole(defaultRole);
        channelRepo.save(channel);
    }

    public List<ChannelsResponse> getAllPublicChannels() {
        return channelRepo.findAllByIsPublic(true).stream().map(channel -> ChannelsResponse.builder()
                .tag(channel.getTag())
                .name(channel.getName())
                .build()).collect(Collectors.toList());
    }

    public List<ChannelsResponse> getAllChannelsForUser(String username) {
        User user = userService.findByUsername(username).get();
        List<Channel> channels = user.getMemberList().stream().filter(
                member -> member.getIsActive().equals(true)
        ).map(ChannelMember::getChannel).toList();
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

    public ChannelInviteResponse getInviteInfo(UUID inviteId) throws NotFoundException {
        ChannelInvite invite = findInviteById(inviteId);
        return ChannelInviteResponse.builder()
                .inviteId(invite.getId())
                .channelName(invite.getChannel().getName())
                .channelTag(invite.getChannel().getTag())
                .expiresAt(invite.getExpiresAt())
                .createdBy(invite.getCreator().getUser().getUsername())
                .build();
    }

    @Transactional
    public void sendSystemMessage(String content, Channel channel) {
        ChannelMember system = channelMemberRepo.findByUser(userService.findByUsername("System").get()).stream().findFirst().get();
        ChannelMessage message = ChannelMessage.builder()
                .id(UUID.randomUUID())
                .channel(channel)
                .author(system)
                .content(content)
                .timestamp(new Date())
                .build();
        channelMessageRepo.save(message);
    }

    /**
     * Creates channel, default roles: Admin, Member - and sets Admin role to the creator of the channel
     * @param creationRequest - must contain: tag, name and creator's username
     */
    @Transactional
    public void createChannel(ChannelCreationRequest creationRequest) throws AlreadyExistException {
        if (channelRepo.findById(creationRequest.getTag()).isPresent())
            throw new AlreadyExistException("Channel with tag " + creationRequest.getTag() + " already exist");
        User user = userService.findByUsername(creationRequest.getCreatorUsername()).get();
        Channel channel = Channel.builder()
                .tag(creationRequest.getTag())
                .name(creationRequest.getName())
                .messageHistory(Collections.emptyList())
                .isPublic(creationRequest.getIsPublic())
                .build();
        channel = channelRepo.save(channel);
        ChannelMember creator = ChannelMember.builder()
                .user(user)
                .channel(channel)
                .roles(List.of(createChannelRole("Admin", channel, List.of(
                        new ChannelPermission(ChannelPermissionNames.SEND_MESSAGES.name(), true),
                        new ChannelPermission(ChannelPermissionNames.EDIT_CHANNEL.name(), true),
                        new ChannelPermission(ChannelPermissionNames.MANAGE_MEMBERS.name(), true),
                        new ChannelPermission(ChannelPermissionNames.CREATE_INVITES.name(), true)
                ))))
                .build();
        channelMemberRepo.save(creator);
        setDefaultChannelRole(channel, createChannelRole("Member", channel, List.of(
                new ChannelPermission(ChannelPermissionNames.SEND_MESSAGES.name(), true),
                new ChannelPermission(ChannelPermissionNames.EDIT_CHANNEL.name(), false),
                new ChannelPermission(ChannelPermissionNames.MANAGE_MEMBERS.name(), false),
                new ChannelPermission(ChannelPermissionNames.CREATE_INVITES.name(), false)
        )));
        sendSystemMessage(SystemMessages.getChannelCreatedMessage(channel.getName()), channel);
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

    @Transactional
    public void joinChannel(String username, String channelTag) throws UserAlreadyInChannelException, NotFoundException, AccessDeniedException {
        User user = userService.findByUsername(username).get();
        Channel channel = findByTag(channelTag);
        if (!channel.getIsPublic()) throw new AccessDeniedException();
        createNewChannelMember(channel, user);
    }

    @Transactional
    public void joinChannelByInvite(String username, UUID inviteId) throws NotFoundException, InviteExpiredException, UserAlreadyInChannelException {
        ChannelInvite invite = findInviteById(inviteId);
        if (invite.getExpiresAt().before(new Date())) {
            channelInviteRepo.delete(invite);
            throw new InviteExpiredException();
        }
        createNewChannelMember(invite.getChannel(), userService.findByUsername(username).get());
    }

    public void createNewChannelMember(Channel channel, User user) throws UserAlreadyInChannelException {
        ChannelMember oldMember = channelMemberRepo.findByChannelAndUser(channel, user).orElse(null);
        if (oldMember != null) {
            if (!oldMember.getIsActive()) {
                oldMember.setIsActive(true);
                channelMemberRepo.save(oldMember);
                sendSystemMessage(SystemMessages.getUserJoinChannelMessage(user.getUsername()), channel);
                return;
            }
            else throw new UserAlreadyInChannelException("User with username " + user.getUsername() + " already participate in channel " + channel.getTag());
        }
        ChannelRole role = getDefaultChannelRole(channel);
        ChannelMember newMember = ChannelMember.builder()
                .roles(List.of(role))
                .user(user)
                .channel(channel)
                .build();
        channelMemberRepo.save(newMember);
        sendSystemMessage(SystemMessages.getUserJoinChannelMessage(user.getUsername()), channel);
    }

    @Transactional
    public ChannelInviteResponse createChannelInvite(String channelTag, String issuerUsername) throws NotFoundException, AccessDeniedException {
        Channel channel = findByTag(channelTag);
        ChannelMember issuer = channelMemberRepo.findByChannelAndUser(channel, userService.findByUsername(issuerUsername).get()).orElseThrow(
                () -> new NotFoundException("User with username " + issuerUsername + " doesn't participate in channel " + channelTag)
        );
        if (!isMemberHasPermission(issuer, ChannelPermissionNames.CREATE_INVITES) || !issuer.getIsActive())
            throw new AccessDeniedException();
        ChannelInvite invite = ChannelInvite.builder()
                .id(UUID.randomUUID())
                .channel(channel)
                .expiresAt(new Date(new Date().getTime() + 3600000))
                .creator(issuer)
                .build();
        return getInviteInfo(channelInviteRepo.save(invite).getId());
    }

    @Transactional
    public void createNewMessage(MessageDto messageDto, String channelTag) throws NotFoundException, AccessDeniedException {
        Channel channel = findByTag(channelTag);
        User user = userService.findByUsername(messageDto.getAuthorUsername()).get();
        ChannelMember author = channelMemberRepo.findByUserAndChannel(user, channel).get();
        if (!isMemberHasPermission(author, ChannelPermissionNames.SEND_MESSAGES) || !author.getIsActive())
            throw new AccessDeniedException();
        ChannelMessage newMessage = ChannelMessage.builder()
                .id(UUID.randomUUID())
                .channel(channel)
                .author(author)
                .content(messageDto.getContent())
                .timestamp(new Date())
                .build();
        channelMessageRepo.save(newMessage);
    }

    public boolean isMemberHasPermission(ChannelMember member, ChannelPermissionNames permissionName) {
        for (ChannelRole role : member.getRoles()) {
            for (ChannelPermission permission : role.getPermissions()) {
                if (permission.getName().equals(permissionName.name()) && permission.isValue()) return true;
            }
        }
        return false;
    }


    public ChannelResponse getChannelInfo(ChannelRequest request) throws NotFoundException, AccessDeniedException {
        Channel channel = findByTag(request.getTag());
        ChannelMember member = channelMemberRepo.findByUserAndChannel(
                userService.findByUsername(request.getUsername()).get(), channel).orElseThrow(
                AccessDeniedException::new
        );
        if (!member.getIsActive()) throw new AccessDeniedException();

        List<ChannelRole> roles = member.getRoles();
        HashMap<String, Boolean> permissions = new HashMap<>();
        for (ChannelRole role : roles) {
            for (ChannelPermission permission : role.getPermissions()) {
                if (permissions.get(permission.getName()) == null || !permissions.get(permission.getName())) {
                    permissions.put(permission.getName(), permission.isValue());
                }
            }
        }

        return ChannelResponse.builder()
                .tag(channel.getTag())
                .name(channel.getName())
                .memberRoles(roles.stream().map(ChannelRole::getName).collect(Collectors.toList()))
                .memberPermissions(permissions)
                .messages(getMessages(channel, request.getPageable()))
                .build();
    }

    public boolean isUserParticipateInChannel(String username, String channelTag) throws NotFoundException {
        Channel channel = findByTag(channelTag);
        ChannelMember member = channelMemberRepo.findByUserAndChannel(userService.findByUsername(username).get(), channel).orElse(null);
        return member != null && member.getIsActive();
    }

    public List<MessageDto> getNewMessages(String channelTag, UUID lastMessageId) throws NotFoundException {
        Channel channel = findByTag(channelTag);
        ChannelMessage lastMessage = channelMessageRepo.findById(lastMessageId).orElseThrow(
                () -> new NotFoundException("Message with id " + lastMessageId + " doesn't exist")
        );
        return channelMessageRepo.findAllByChannelAndTimestampGreaterThanOrderByTimestamp(channel, lastMessage.getTimestamp())
                .stream().map(
                        message -> MessageDto.builder()
                                .messageId(message.getId())
                                .authorUsername(message.getAuthor().getUser().getUsername())
                                .content(message.getContent())
                                .timestamp(message.getTimestamp())
                                .build()
        ).collect(Collectors.toList());
    }

    @Transactional
    public void removeMemberFromChannel(String username, String channelTag) throws NotFoundException {
        Channel channel = findByTag(channelTag);
        ChannelMember member = channelMemberRepo.findByUserAndChannel(userService.findByUsername(username).get(), channel).orElseThrow(
                () -> new NotFoundException("User with username " + username + " doesn't participate in channel " + channelTag)
        );
        member.setIsActive(false);
        channelMemberRepo.save(member);
        sendSystemMessage(SystemMessages.getUserLeftChannelMessage(username), channel);
    }

    public void kickMember(String channelTag, String issuerUsername, String memberUsername) throws NotFoundException, AccessDeniedException {
        Channel channel = findByTag(channelTag);
        ChannelMember issuer = channelMemberRepo.findByUserAndChannel(userService.findByUsername(issuerUsername).get(), channel)
                .orElse(null);
        ChannelMember member = channelMemberRepo.findByUserAndChannel(userService.findByUsername(memberUsername).orElseThrow(
                () -> new NotFoundException("User with username " + memberUsername + " doesn't exist")
        ), channel).orElse(null);

        if (issuer == null || !issuer.getIsActive())
            throw new AccessDeniedException();
        if (member == null || !member.getIsActive())
            throw new NotFoundException(memberUsername + " doesn't participate in channel " + channelTag);
        if (!isMemberHasPermission(issuer, ChannelPermissionNames.MANAGE_MEMBERS))
            throw new AccessDeniedException();

        removeMemberFromChannel(member.getUser().getUsername(), channel.getTag());
    }
}
