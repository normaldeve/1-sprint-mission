package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;

public interface UserService {
    User createUser(String name, String phone, String password);

    User  getUserById(String phone);

    List<User> getAllUser();

    User updateUserPassword(String phone, String newPass);

    boolean deleteUser(String phone);

}
