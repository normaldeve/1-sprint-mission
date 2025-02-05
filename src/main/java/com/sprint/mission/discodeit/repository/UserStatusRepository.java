package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.UserStatus;

import java.util.UUID;

public interface UserStatusRepository {
    UserStatus save(UserStatus userStatus);

    UserStatus findByUserId(UUID userId);
}
