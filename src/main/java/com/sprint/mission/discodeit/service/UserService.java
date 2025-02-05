package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.User;
import com.sprint.mission.discodeit.dto.user.FindUserDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User create(UserCreateRequest request);

    FindUserDto getUserByPhone(String phone);

    List<User> getAllUser();

    User updateUserPassword(User user, String newPass);

    void delete(User removeUser);
}
