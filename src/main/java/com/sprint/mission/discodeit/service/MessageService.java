package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.Channel;
import com.sprint.mission.discodeit.domain.Message;
import com.sprint.mission.discodeit.domain.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    Message create(String content, User writer, Channel channel);

    Optional<Message> getMessage(UUID messageID);

    List<Message> getMessageWithWriter(User writer);

    List<Message> getMessageWithChannel(Channel channel);

    List<Message> getAllMessage();

    Message updateMessageContent(Message message, String newContent);

    void deleteMessage(UUID messageID);
}
