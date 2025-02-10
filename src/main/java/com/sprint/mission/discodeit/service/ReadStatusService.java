package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatus create();

    Optional<ReadStatus> find(UUID id);

    List<ReadStatus> findAllByUserId(UUID userID);

    ReadStatus update();

    void delete(UUID id);
}
