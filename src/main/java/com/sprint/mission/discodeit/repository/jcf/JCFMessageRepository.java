package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


import static com.sprint.mission.discodeit.error.MessageError.EMPTY_CONTENT;


public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> messageMap;

    public JCFMessageRepository() {
        this.messageMap = new HashMap<>();
    }

    @Override
    public Message create(String content, User writer, Channel channel) {
        if (content.isEmpty()) {
            throw new IllegalArgumentException(EMPTY_CONTENT.getMessage());
        }
        Message message = new Message(content, writer, channel);
        messageMap.put(message.getId(), message);
        return message;
    }
}
