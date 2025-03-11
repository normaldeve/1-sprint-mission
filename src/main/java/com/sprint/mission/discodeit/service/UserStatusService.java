package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusDTO;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusDTO create(UUID userId);

    UserStatusDTO find(UUID id);

    UserStatusDTO findByUserId(UUID userID);

    List<UserStatusDTO> findAll();

    UserStatusDTO updateByUserId(UUID userId);

    void delete(UUID id);
}
