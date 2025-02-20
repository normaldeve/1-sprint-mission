package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.BinaryContent;
import com.sprint.mission.discodeit.dto.binarycontent.SaveFileRequest;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContent saveFile(SaveFileRequest request) throws IOException;

    Optional<BinaryContent> find(UUID id);

    List<BinaryContent> findAllByIdIn(List<UUID> uuidList);

    List<BinaryContent> findAll();

    void delete(UUID id);
}
