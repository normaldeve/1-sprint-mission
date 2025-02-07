package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.User;
import com.sprint.mission.discodeit.dto.user.CreateUser;
import com.sprint.mission.discodeit.dto.user.FindUserDto;
import com.sprint.mission.discodeit.dto.user.UpdateUser;
import com.sprint.mission.discodeit.dto.user.UserDTO;

import java.util.List;
import java.util.UUID;

public interface UserService {
    CreateUser.Response create(CreateUser.Request request);

    UserDTO find(UUID userId);

    List<UserDTO> findAll();

    UpdateUser.Response update(UpdateUser.Request request);

    void delete(User removeUser);
}
