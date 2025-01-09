package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.UUID;

public interface MessageService {

    void createMessage(Message message);

    Message getMessageById(UUID id);

    Message updateMessageId(UUID id, String newContent);

    void deleteMessage(Message message);

}
