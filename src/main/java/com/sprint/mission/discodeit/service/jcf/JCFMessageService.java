package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

import static com.sprint.mission.discodeit.error.MessageError.*;


public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messageRepository;
    private static final MessageService instance = new JCFMessageService();

    private JCFMessageService() {
        this.messageRepository = new HashMap<>();
    }

    public static MessageService getInstance() {
        return instance;
    }

    @Override
    public Message createMessage(String content, User writer, Channel channel) throws IllegalArgumentException {
        if (content.isEmpty()) {
            throw new IllegalArgumentException(EMPTY_CONTENT.getMessage());
        }

        Message message = new Message(content, writer, channel);
        messageRepository.put(message.getId(), message);
        return message;
    }

    // 메시지를 보낸 회원이 메시지 조회하기
    @Override
    public List<Message> getMessageByUser(User writer) {
        List<Message> messages = new ArrayList<>();
        for (Message message : messageRepository.values()) {
            if (message.getWriter().equals(writer)) {
                messages.add(message);
            }
        }
        if (!messages.isEmpty()) {
            return messages;
        }

        throw new IllegalArgumentException(EMPTY_MESSAGE.getMessage());
    }

    @Override
    public List<Message> getMessageByChannel(String channelName) {
        List<Message> messages = new ArrayList<>();
        for (Message message : messageRepository.values()) {
            if (message.getChannel().getName() == channelName) {
                messages.add(message);
            }
        }
        if (!messages.isEmpty()) {
            return messages;
        }
        throw new IllegalArgumentException(EMPTY_MESSAGE.getMessage());
    }


    @Override
    public Message updateMessageContent(UUID id, String newContent) {
        Message findMessage = messageRepository.get(id);
        if (newContent.isEmpty()) {
            throw new IllegalArgumentException(EMPTY_CONTENT.getMessage());
        }
        findMessage.update(newContent);
        return findMessage;
    }

    @Override
    public List<Message> getAllMessages() {
        if (messageRepository.values() == null) {
            throw new IllegalArgumentException(EMPTY_MESSAGE.getMessage());
        }
        return new ArrayList<>(messageRepository.values());
    }

    @Override
    public boolean deleteMessageByWriter(User writer, UUID messageId) { // 작성자만 본인 메시지를 삭제할 수 있다.
        for (Message message : messageRepository.values()) {
            if (message.getWriter().equals(writer) && message.getId() == messageId) {
                messageRepository.remove(messageId);
                return true;
            }
        }
        throw new IllegalArgumentException(EMPTY_MESSAGE.getMessage());
    }

    @Override
    public void deleteAllMessageByWriter(User writer) {
        for (Message message : messageRepository.values()) {
            if (message.getWriter().getPhone().equals(writer.getPhone())) {
                messageRepository.remove(message.getId());
            }
        }
    }

    @Override
    public void deleteAllMessageByChannel(Channel channel) {
        for (Message message : messageRepository.values()) {
            if (message.getChannel().getName().equals(channel.getName())) {
                messageRepository.remove(message.getId());
            }
        }
    }
}
