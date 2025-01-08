package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

public interface MessageService {

    void createMessage();

    Message getMessageById();

    Message updateMessage();

    void deleteMessage();

}
