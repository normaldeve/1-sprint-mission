package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.*;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDTO create(CreateUserRequest request);

    UserDTO find(UUID userId);

    List<UserDTO> findAll();

    UserDTO update(UpdateUser request);

    UserDTO delete(UUID id);
}
