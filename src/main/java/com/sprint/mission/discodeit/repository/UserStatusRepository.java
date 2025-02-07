package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.UserStatus;

import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {
    UserStatus save(UserStatus userStatus);

    Optional<UserStatus> findById(UUID uuid);

    void deleteById(UUID uuid);
}
