package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContent create(BinaryContentCreateRequest request) throws IOException;

    BinaryContent find(UUID binaryContentId);

    List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds);

    void delete(UUID id);
}
