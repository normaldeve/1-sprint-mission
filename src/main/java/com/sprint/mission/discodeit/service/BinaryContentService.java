package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.type.BinaryContentUploadStatus;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

  BinaryContentDto create(BinaryContentCreateRequest request);

  BinaryContentDto find(UUID binaryContentId);

  List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds);

  void delete(UUID binaryContentId);

  void updateStatus(UUID id, BinaryContentUploadStatus status);
}
