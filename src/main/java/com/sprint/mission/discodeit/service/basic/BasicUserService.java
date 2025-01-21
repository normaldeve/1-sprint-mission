package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Optional;

public class BasicUserService implements UserService{
    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void setDependency(MessageService messageService, ChannelService channelService) {

    }

    @Override
    public User createUser(String name, String phone, String password) {
        return null;
    }

    @Override
    public Optional<User> getUserByPhone(String phone) {
        return Optional.empty();
    }

    @Override
    public boolean userExists(String phone) {
        return false;
    }

    @Override
    public List<User> getAllUser() {
        return List.of();
    }

    @Override
    public User updateUserPassword(User user, String newPass) {
        return null;
    }

    @Override
    public void deleteUser(User removeUser) {

    }
}
