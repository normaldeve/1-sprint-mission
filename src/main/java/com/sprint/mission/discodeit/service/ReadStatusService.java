package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.ReadStatus;
import com.sprint.mission.discodeit.dto.readstatus.CreateReadStatusRequest;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

  ReadStatus create(CreateReadStatusRequest request);

  ReadStatus find(UUID id);

  List<ReadStatus> findAllByUserId(UUID userID);

  List<ReadStatus> updateByChannelId(UUID channelId);

  ReadStatus update(UUID id);

  void delete(UUID id);
}
