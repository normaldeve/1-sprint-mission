package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messageRepository;

    public JCFMessageService() {
        this.messageRepository = new HashMap<>();
    }

    @Override
    public void createMessage(Message message) {
        if (messageRepository.get(message.getId()) == null) {
            messageRepository.put(message.getId(), message);
            System.out.println("메시지 생성이 완료되었습니다");
        } else {
            System.out.println("이미 존재하는 메시지입니다");
        }
    }

    @Override
    public Message getMessageById(UUID id) {
        if (messageRepository.get(id) != null) {
            return messageRepository.get(id);
        } else {
            System.out.println("유효하지 않는 ID이거나 존재하지 않는 메시지입니다");
            return null;
        }
    }

    @Override
    public Message updateMessageId(UUID id, String newContent) {
        if (messageRepository.get(id) != null) {
            Message message = messageRepository.get(id);
            message.update(newContent);
            return message;
        } else {
            System.out.println("유효하지 않는 ID이거나 존재하지 않는 메시지입니다");
            return null;
        }
    }

    @Override
    public void deleteMessage(Message message) {
        if (messageRepository.get(message.getId()) != null) {
            messageRepository.remove(message.getId());
            System.out.println("메시지 삭제가 완료되었습니다");
        } else {
            System.out.println("존재하지 않는 메시지입니다");
        }
    }
}
