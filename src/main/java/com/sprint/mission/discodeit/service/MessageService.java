package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message createMessage(String content, User writer, Channel channel);

    List<Message> getMessageByUser(User writer);

    List<Message> getMessageByChannel(Channel channel);

    Message updateMessageContent(UUID id, String newContent);

    void removeMessageByWriter(User writer, UUID uuid);

    void deleteMessageWithWriter(User writer); // 작성자가 특정 메시지를 삭제하기

    void deleteMessageWithChannel(Channel channel);
}
