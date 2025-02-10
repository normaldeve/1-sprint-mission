package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.BinaryContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContent create();

    Optional<BinaryContent> find(UUID id);

    List<BinaryContent> findAllByIdInUser(UUID userId);

    List<BinaryContent> findAllByIdInMessage(UUID messageId);

    void delete(UUID id);


}
