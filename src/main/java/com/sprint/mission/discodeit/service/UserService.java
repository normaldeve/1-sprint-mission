package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

public interface UserService {
    void createUser();

    User getUserById();

    User updateUser();

    void deleteUser();

}
