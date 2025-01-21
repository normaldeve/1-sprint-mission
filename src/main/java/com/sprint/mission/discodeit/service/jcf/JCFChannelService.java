package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.error.ErrorCode;
import com.sprint.mission.discodeit.exception.ServiceException;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;


import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> channelRepository;
    private UserService userService;
    private MessageService messageService;

    @Override
    public void setDependency(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }

    @Override
    public Channel createChannel(String name, User creator) throws IllegalArgumentException {
        if (channelRepository.values().stream()
                .anyMatch(user -> user.getName().equals(name))) {
            throw new ServiceException(ErrorCode.DUPLICATE_CHANNEL);
        }

        Channel createChannel = new Channel(name, creator);
        channelRepository.put(createChannel.getId(), createChannel);
        return createChannel;
    }

    @Override
    public Optional<Channel> getChannelByName(String name) {
        for (Channel channel : channelRepository.values()) {
            if (channel.getName().equals(name)) {
                return Optional.of(channel);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean channelExists(String name) {
        return channelRepository.values().stream()
                .anyMatch(user -> user.getName().equals(name));
    }

    @Override
    public List<Channel> getAllChannel() {
        return channelRepository.values().stream()
                .collect(Collectors.toList());
    }

    @Override
    public List<Channel> getChannelsByUserId(User user) {
        return channelRepository.values().stream()
                .filter(channel -> channel.getMembers().stream()
                        .anyMatch(member -> Objects.equals(member.getPhone(), user.getPhone()))
                )
                .collect(Collectors.toList());
    }


    @Override
    public Channel addUserToChannel(Channel channel, User newUser) {//새로운 유저가 채널에 들어갈때
        validateChannel(channel);
        validateUser(newUser);
        channel.addUser(newUser);
        return channel;
    }

    @Override
    public Channel addManyUserToChannel(Channel channel, List<User> users) {
        validateChannel(channel);
        channel.addManyUser(users);
        return channel;
    }

    @Override
    public Channel removeUserToChannel(Channel channel, User removeUser) {
        validateChannel(channel);
        validateUser(removeUser);
        channel.removeUser(removeUser);
        return channel;
    }

    @Override
    public void deleteChannel(Channel channel) { // 채널이 사라지면 해당 채널에 포함된 메시지도 사라진다.
        validateChannel(channel);

        messageService.deleteMessageWithChannel(channel);
        channelRepository.remove(channel.getId());
    }

    private void validateUser(User user) {
        User findUser = userService.getUserByPhone(user.getPhone())
                .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USER));
    }

    private void validateChannel(Channel channel) {
        Channel findChannel = getChannelByName(channel.getName())
                .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_CHANNEL));
    }
}