package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    void setDependency(UserService userService, ChannelService channelService);

    Message createMessage(String content, User writer, Channel channel);

    List<Message> getMessageByUser(User writer);

    List<Message> getMessageByChannel(Channel channel);

    Message updateMessageContent(Message message, String newContent);

    void removeMessageByWriter(User writer, UUID uuid);

    void deleteMessageWithChannel(Channel channel);
}
