package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.domain.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicReadStatusService implements ReadStatusService {

    @Override
    public ReadStatus create() {
        return null;
    }

    @Override
    public Optional<ReadStatus> find(UUID id) {
        return Optional.empty();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userID) {
        return List.of();
    }

    @Override
    public ReadStatus update() {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }
}
