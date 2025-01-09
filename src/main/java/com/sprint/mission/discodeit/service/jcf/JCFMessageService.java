package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;


public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messageRepository;
    private UserService userService;

    public JCFMessageService(UserService userService) {
        this.messageRepository = new HashMap<>();
        this.userService = userService;
    }

    @Override
    public Message createMessage(String content, User fromUser, User toUser) {
        List<User> users = userService.getAllUser();
        if (!(users.contains(fromUser) && users.contains(toUser))) {
            System.out.println("수신자와 송신자를 확인해주세요");
            return null;
        }
        Message message = new Message(content, fromUser, toUser);
        messageRepository.put(message.getId(), message);
        System.out.println("메시지 생성이 완료되었습니다");
        return message;
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
    public List<Message> getAllMessages() {
        return new ArrayList<>(messageRepository.values());
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
