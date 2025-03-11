package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {

  Message create(CreateMessageRequest messageRequest,
      List<CreateBinaryContentRequest> binaryContentRequests);

  Message find(UUID messageId);

  List<Message> findAllByChannelId(UUID channelID);

  Message update(UUID messageId, UpdateMessageRequest request;

  void delete(UUID messageID);
}
