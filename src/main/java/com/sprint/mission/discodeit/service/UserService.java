package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(String name, String phone, String password);

    Optional<User> getUserByPhone(String phone);

    List<User> getAllUser();

    User updateUserPassword(User user, String newPass);

    void deleteUser(User removeUser);
}
