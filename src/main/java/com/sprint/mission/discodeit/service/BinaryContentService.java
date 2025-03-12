package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDTO;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentDTO create(BinaryContentCreateRequest request) throws IOException;

    BinaryContentDTO find(UUID binaryContentId);

    List<BinaryContentDTO> findAllByIdIn(List<UUID> binaryContentIds);

    void delete(UUID binaryContentId);
}
