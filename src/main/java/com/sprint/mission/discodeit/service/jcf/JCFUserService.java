package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.ValidPass;
import com.sprint.mission.discodeit.util.ValidPhone;

import java.util.*;

import static com.sprint.mission.discodeit.error.UserError.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> userRepository;
    private MessageService messageService;
    private ChannelService channelService;

    public JCFUserService() {
        this.userRepository = new HashMap<>();
    }

    @Override
    public void setDependency(MessageService messageService, ChannelService channelService) {
        this.messageService = messageService;
        this.channelService = channelService;
    }

    @Override
    public User createUser(String name, String phone, String password) throws IllegalArgumentException {
        if (!ValidPass.isValidPassword(password)) {
            throw new IllegalArgumentException(INVALID_PASSWORD.getMessage());
        }

        if (!ValidPhone.isValidPhone(phone)) {
            throw new IllegalArgumentException(INVALID_PHONE.getMessage());
        }

        if (userRepository.values().stream()
                .anyMatch(user -> user.getPhone().equals(phone))) {
            throw new IllegalArgumentException(DUPLICATE_PHONE.getMessage());
        }
        User user = new User(name, phone, password);
        userRepository.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> getUserByPhone(String phone) {
        return userRepository.values().stream()
                .filter(user -> user.getPhone().equals(phone))
                .findFirst();
    }

    @Override
    public List<User> getAllUser() {
        return new ArrayList<>(userRepository.values());
    }

    @Override
    public User updateUserPassword(User updateUser, String newPass) {
        if (!userExists(updateUser.getPhone())) {
            throw new IllegalArgumentException(CANNOT_FOUND_USER.getMessage());
        }
        User findUser = userRepository.get(updateUser.getId());
        findUser.update(newPass);
        return findUser;
    }

    @Override
    public void deleteUser(User removeUser) { // 유저 정보 삭제 시 유저가 속해있던 채널에 해당 유저가 삭제되어야 한다.
        if (!userExists(removeUser.getPhone())) {
            throw new IllegalArgumentException(CANNOT_FOUND_USER.getMessage());
        }
        channelService.getAllChannel().stream()
                .forEach(channel -> channel.getMembers()
                        .removeIf(user -> user.getId().equals(removeUser.getId())));
        userRepository.remove(removeUser.getId());
    }

    @Override
    public boolean userExists(String phone) {
        return userRepository.values()
                .stream()
                .anyMatch(user -> user.getPhone().equals(phone));
    }
}
