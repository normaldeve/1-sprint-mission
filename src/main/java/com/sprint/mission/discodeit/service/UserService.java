package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void setDependency(MessageService messageService, ChannelService channelService);

    User create(String name, String phone, String password);

    Optional<User> getUserByPhone(String phone);

    boolean userExists(String phone);

    List<User> getAllUser();

    User updateUserPassword(User user, String newPass);

    void deleteUser(User removeUser);

}
