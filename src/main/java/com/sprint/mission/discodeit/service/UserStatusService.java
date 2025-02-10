package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusService {
    UserStatus create();

    Optional<UserStatus> find(UUID id);

    List<UserStatus> findAll();

    UserStatus update();

    UserStatus updateByUserId(UUID userId);

    void delete(UUID id);
}
