package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message createMessage(String content, User fromUser, User toUser);

    Message getMessageById(UUID id);

    Message updateMessageId(UUID id, String newContent);

    List<Message> getAllMessages();

    void deleteMessage(Message message);

}
