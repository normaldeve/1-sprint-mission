package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.domain.UserStatus;
import com.sprint.mission.discodeit.service.UserStatusService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicUserStatusService implements UserStatusService {
    @Override
    public UserStatus create() {
        return null;
    }

    @Override
    public Optional<UserStatus> find(UUID id) {
        return Optional.empty();
    }

    @Override
    public List<UserStatus> findAll() {
        return List.of();
    }

    @Override
    public UserStatus update() {
        return null;
    }

    @Override
    public UserStatus updateByUserId(UUID userId) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }
}
