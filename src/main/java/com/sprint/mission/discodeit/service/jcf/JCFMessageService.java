package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.factory.Factory;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

import static com.sprint.mission.discodeit.error.ChannelError.CANNOT_FOUND_CHANNEL;
import static com.sprint.mission.discodeit.error.MessageError.*;
import static com.sprint.mission.discodeit.error.UserError.CANNOT_FOUND_USER;


public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messageRepository;
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService() {
        this.messageRepository = new HashMap<>();
        this.userService = Factory.getUserService();
        this.channelService = Factory.getChannelService();
    }

    @Override
    public Message createMessage(String content, User writer, Channel channel) throws IllegalArgumentException {
        if (content.isEmpty()) {
            throw new IllegalArgumentException(EMPTY_CONTENT.getMessage());
        }

        if (userService.userExists(writer.getId())) { // 해당 작성자가 존재하는가
            throw new IllegalArgumentException(CANNOT_FOUND_USER.getMessage());
        }

        if (channelService.channelExist(channel.getId())) { // 해당 채널이 존재하는가?
            throw new IllegalArgumentException(CANNOT_FOUND_CHANNEL.getMessage());
        }

        Message message = new Message(content, writer, channel);
        messageRepository.put(message.getId(), message);
        return message;
    }

    // 메시지를 보낸 회원이 메시지 조회하기
    @Override
    public List<Message> getMessageByUser(User writer) {
        if (userService.userExists(writer.getId())) {
            throw new IllegalArgumentException(CANNOT_FOUND_USER.getMessage());
        }
        List<Message> messages = new ArrayList<>();
        for (Message message : messageRepository.values()) {
            if (message.getWriter().getPhone().equals(writer.getPhone())) {
                messages.add(message);
            }
        }
        return messages;
    }

    @Override
    public List<Message> getMessageByChannel(Channel channel) {
        if (channelService.channelExist(channel.getId())) {
            throw new IllegalArgumentException(CANNOT_FOUND_CHANNEL.getMessage());
        }
        List<Message> messages = new ArrayList<>();
        for (Message message : messageRepository.values()) {
            if (message.getChannel().getName().equals(channel.getName())) {
                messages.add(message);
            }
        }
        return messages;
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
    public void removeMessageByWriter(User writer, UUID uuid) { // 작성자가 작성한 메시지 삭제하기
        if (!messageRepository.containsKey(uuid)) {
            throw new IllegalArgumentException(CANNOT_FOUND_MESSAGE.getMessage());
        }
        Message findMessage = messageRepository.get(uuid);
        if (!findMessage.getWriter().getId().equals(writer.getId())) {
            throw new IllegalArgumentException(INVALID_WRITER.getMessage());
        }
        messageRepository.remove(uuid);
    }


    @Override
    public void deleteMessageWithWriter(User writer) { // 유저가 삭제될 때 메시지도 삭제되는 메서드
        Iterator<Message> iterator = messageRepository.values().iterator();
        while (iterator.hasNext()) {
            Message message = iterator.next();
            if (Objects.equals(message.getWriter().getId(), writer.getId())) {
                iterator.remove();
            }
        }
    }


    @Override
    public void deleteMessageWithChannel(Channel channel) { // 채널이 삭제될 때 메시지도 없애는 메소드
        Iterator<Message> iterator = messageRepository.values().iterator();
        while (iterator.hasNext()) {
            Message message = iterator.next();
            if (Objects.equals(message.getChannel().getId(), channel.getId())) {
                iterator.remove();
            }
        }
    }
}
