package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageDTO;
import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;

import java.util.List;
import java.util.UUID;

public interface MessageService {

  MessageDTO create(CreateMessageRequest messageRequest,
                    List<CreateBinaryContentRequest> binaryContentRequests);

  MessageDTO find(UUID messageId);

  List<MessageDTO> findAllByChannelId(UUID channelID);

  MessageDTO update(UUID messageId, UpdateMessageRequest request);

  void delete(UUID messageID);
}
