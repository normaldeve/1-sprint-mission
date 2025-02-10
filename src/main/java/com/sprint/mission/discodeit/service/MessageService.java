package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.Channel;
import com.sprint.mission.discodeit.domain.Message;
import com.sprint.mission.discodeit.domain.User;
import com.sprint.mission.discodeit.dto.message.CreateMessage;
import com.sprint.mission.discodeit.dto.message.UpdateMessage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    Message create(CreateMessage.Request request);

    List<Message> findAllByChannelId(UUID channelID);

    List<Message> getAllMessage();

    Message updateMessageContent(UpdateMessage.Request request);

    void deleteMessage(UUID messageID);
}
