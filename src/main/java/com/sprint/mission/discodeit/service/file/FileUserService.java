package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.ValidPass;
import com.sprint.mission.discodeit.util.ValidPhone;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.sprint.mission.discodeit.error.UserError.*;
import static com.sprint.mission.discodeit.util.FileIOUtil.*;

public class FileUserService implements UserService {
    private final Path filePath;
    private MessageService messageService;
    private ChannelService channelService;

    public FileUserService(String filePath) {
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
    public void setDependency(MessageService messageService, ChannelService channelService) {
        this.messageService = messageService;
        this.channelService = channelService;
    }

    @Override
    public User createUser(String name, String phone, String password) {
        if (!ValidPass.isValidPassword(password)) {
            throw new IllegalArgumentException(INVALID_PASSWORD.getMessage());
        }

        if (!ValidPhone.isValidPhone(phone)) {
            throw new IllegalArgumentException(INVALID_PHONE.getMessage());
        }
        Map<UUID, User> users = loadFromFile(filePath);
        if (users.values().stream()
                .anyMatch(user -> user.getPhone().equals(phone))) {
            throw new IllegalArgumentException(DUPLICATE_PHONE.getMessage());
        }

        User createUser = new User(name, phone, password);
        users.put(createUser.getId(), createUser);
        saveToFile(users, filePath);
        return createUser;
    }

    @Override
    public Optional<User> getUserByPhone(String phone) {
        Map<UUID, User> users = loadFromFile(filePath);
        return users.values().stream()
                .filter(user -> user.getPhone().equals(phone))
                .findFirst();
    }

    @Override
    public boolean userExists(String phone) {
        Map<UUID, User> users = loadFromFile(filePath);
        for (User user : users.values()) {
            if (user.getPhone() == phone) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<User> getAllUser() {
        Map<UUID, User> users = loadFromFile(filePath);
        return users.values()
                .stream()
                .collect(Collectors.toList());
    }


    @Override
    public User updateUserPassword(User updateUser, String newPass) {
        Map<UUID, User> users = loadFromFile(filePath);
        if (!userExists(updateUser.getPhone())) {
            throw new IllegalArgumentException(CANNOT_FOUND_USER.getMessage());
        }
        User findUser = users.get(updateUser.getId());
        findUser.update(newPass);
        return findUser;
    }

    @Override
    public void deleteUser(User removeUser) {
        Map<UUID, User> users = loadFromFile(filePath);
        if (!userExists(removeUser.getPhone())) {
            throw new IllegalArgumentException(CANNOT_FOUND_USER.getMessage());
        }
        channelService.getAllChannel().stream()
                .forEach(channel -> channel.getMembers()
                        .removeIf(user -> user.getId().equals(removeUser.getId())));
        users.remove(removeUser.getId());
    }
}
