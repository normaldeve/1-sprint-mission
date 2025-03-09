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
      Optional<CreateBinaryContentRequest> binaryContentRequest);

  List<Message> findAllByChannelId(UUID channelID);

  List<Message> getAllMessage();

  Message updateMessageContent(UUID messageId, UpdateMessageRequest request,
      Optional<CreateBinaryContentRequest> binaryContentRequest);

  Message deleteMessage(UUID messageID);
}
