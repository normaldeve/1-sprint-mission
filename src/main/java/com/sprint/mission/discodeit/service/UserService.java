package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User createUser(String name, String phone, String password);

    User getUserById(UUID id);

    List<User> getAllUser();

    User updateUserPasswordById(UUID id, String newPass);

    void deleteUser(User user);

}
