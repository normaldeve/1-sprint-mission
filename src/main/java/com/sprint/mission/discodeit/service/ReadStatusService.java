package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDTO;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.dto.readstatus.CreateReadStatusRequest;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

  ReadStatusDTO create(CreateReadStatusRequest request);

  ReadStatusDTO find(UUID readStatusId);

  List<ReadStatusDTO> findAllByUserId(UUID userID);

  List<ReadStatusDTO> updateByChannelId(UUID channelId);

  ReadStatusDTO update(UUID readStatusId);

  void delete(UUID readStatusId);
}
