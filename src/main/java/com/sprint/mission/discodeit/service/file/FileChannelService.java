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
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sprint.mission.discodeit.util.FileIOUtil.saveToFile;

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
                throw new RuntimeException("Error initializing user repository file", e);
            }
        }
    }

    @Override
    public void setDependency(UserService userService, MessageService messageService) {

    }

    @Override
    public Channel createChannel(String name, User creator) {
        return null;
    }

    @Override
    public Optional<Channel> getChannelByName(String name) {
        return Optional.empty();
    }

    @Override
    public boolean channelExist(String name) {
        return false;
    }

    @Override
    public List<Channel> getAllChannel() {
        return List.of();
    }

    @Override
    public List<Channel> getChannelsByUserId(User user) {
        return List.of();
    }

    @Override
    public Channel addUserToChannel(Channel channel, User newUser) {
        return null;
    }

    @Override
    public Channel addManyUserToChannel(Channel channel, List<User> users) {
        return null;
    }

    @Override
    public Channel removeUserToChannel(Channel channel, User removeUser) {
        return null;
    }

    @Override
    public void deleteChannel(Channel removeChannel) {

    }
}
