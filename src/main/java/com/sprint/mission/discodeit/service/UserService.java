package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.UUID;

public interface UserService {
    void createUser(User user);

    User getUserById(UUID id);

    User updateUserPasswordById(UUID id, String newPass);

    void deleteUser(User user);

}
