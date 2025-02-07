package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.*;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDTO create(CreateUser.Request request);

    UserDTO find(UUID userId);

    List<UserDTO> findAll();

    UserDTO update(UpdateUser.Request request);

    UserDTO delete(UUID id);
}
