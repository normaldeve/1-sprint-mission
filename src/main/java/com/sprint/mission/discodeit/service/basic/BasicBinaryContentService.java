package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.domain.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicBinaryContentService implements BinaryContentService {
    @Override
    public BinaryContent create() {
        return null;
    }

    @Override
    public Optional<BinaryContent> find(UUID id) {
        return Optional.empty();
    }

    @Override
    public List<BinaryContent> findAllByIdInUser(UUID userId) {
        return List.of();
    }

    @Override
    public List<BinaryContent> findAllByIdInMessage(UUID messageId) {
        return List.of();
    }

    @Override
    public void delete(UUID id) {

    }
}
