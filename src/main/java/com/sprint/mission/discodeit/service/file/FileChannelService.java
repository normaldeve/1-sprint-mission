package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.sprint.mission.discodeit.util.FileIOUtil.*;
import static com.sprint.mission.discodeit.util.FileIOUtil.saveToFile;

import static com.sprint.mission.discodeit.error.ChannelError.*;
import static com.sprint.mission.discodeit.error.UserError.CANNOT_FOUND_USER;

public class FileChannelService implements ChannelService {
    private final Path filePath;
    private UserService userService;
    private MessageService messageService;

    public FileChannelService(String filePath) {
        this.filePath = Paths.get(filePath);
        if (!Files.exists(this.filePath)) {
            try {
                Files.createFile(this.filePath);
                saveToFile(new HashMap<>(), this.filePath);
            } catch (IOException e) {
                throw new RuntimeException("채널 파일을 초기화 하던 중에 문제가 발생했습니다", e);
            }
        }
    }

    @Override
    public void setDependency(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }

    @Override
    public Channel createChannel(String name, User creator) {
        Map<UUID, Channel> channels = loadFromFile(filePath);
        for (Channel channel : channels.values()) {
            if (channelExist(name)) {
                throw new IllegalArgumentException(DUPLICATE_NAME.getMessage());
            }
        }
        Channel createChannel = new Channel(name, creator);
        channels.put(createChannel.getId(), createChannel);
        saveToFile(channels, filePath);
        return createChannel;
    }

    @Override
    public Optional<Channel> getChannelByName(String name) {
        Map<UUID, Channel> channels = loadFromFile(filePath);
        for (Channel channel : channels.values()) {
            if (channel.getName().equals(name)) {
                return Optional.of(channel);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean channelExist(String name) {
        Map<UUID, Channel> channels = loadFromFile(filePath);
        return channels.values().stream()
                .anyMatch(user -> user.getName().equals(name));
    }

    @Override
    public List<Channel> getAllChannel() {
        Map<UUID, Channel> channels = loadFromFile(filePath);
        return channels.values().stream()
                .collect(Collectors.toList());
    }

    @Override
    public List<Channel> getChannelsByUserId(User user) {
        Map<UUID, Channel> channels = loadFromFile(filePath);
        return channels.values().stream()
                .filter(channel -> channel.getMembers().stream()
                        .anyMatch(member -> Objects.equals(member.getPhone(), user.getPhone()))
                )
                .collect(Collectors.toList());
    }

    @Override
    public Channel addUserToChannel(Channel channel, User newUser) {
        Map<UUID, Channel> channels = loadFromFile(filePath);
        if (!channelExist(channel.getName())) {
            throw new IllegalArgumentException(CANNOT_FOUND_CHANNEL.getMessage());
        }
        if (!userService.userExists(newUser.getPhone())) {
            throw new IllegalArgumentException(CANNOT_FOUND_USER.getMessage());
        }
        channel.addUser(newUser);
        return channel;
    }

    @Override
    public Channel addManyUserToChannel(Channel channel, List<User> users) {
        if (!channelExist(channel.getName())) {
            throw new IllegalArgumentException(CANNOT_FOUND_CHANNEL.getMessage());
        }
        channel.addManyUser(users);
        return channel;
    }

    @Override
    public Channel removeUserToChannel(Channel channel, User removeUser) {
        if (!channelExist(channel.getName())) {
            throw new IllegalArgumentException(CANNOT_FOUND_CHANNEL.getMessage());
        }

        if (!userService.userExists(removeUser.getPhone())) {
            throw new IllegalArgumentException(CANNOT_FOUND_USER.getMessage());
        }
        channel.removeUser(removeUser);
        return channel;
    }

    @Override
    public void deleteChannel(Channel channel) {
        Map<UUID, Channel> channels = loadFromFile(filePath);
        if (!channelExist(channel.getName())) {
            throw new IllegalArgumentException(CANNOT_FOUND_CHANNEL.getMessage());
        }

        messageService.deleteMessageWithChannel(channel);
        channels.remove(channel.getId());

        saveToFile(channels, filePath);
    }
}
