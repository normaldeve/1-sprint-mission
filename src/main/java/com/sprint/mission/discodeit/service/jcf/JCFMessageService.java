package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.error.ErrorCode;
import com.sprint.mission.discodeit.exception.ServiceException;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messageRepository;
    private UserService userService;
    private ChannelService channelService;

    @Override
    public void setDependency(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message createMessage(String content, User writer, Channel channel) throws IllegalArgumentException {
        if (content.isEmpty()) {
            throw new ServiceException(ErrorCode.EMPTY_CONTENT);
        }

        validateUser(writer);

        validateChannel(channel);

        Message message = new Message(content, writer, channel);
        messageRepository.put(message.getId(), message);
        return message;
    }

    // 메시지를 보낸 회원이 메시지 조회하기
    @Override
    public List<Message> getMessageByUser(User writer) {
        validateUser(writer);
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
        List<Message> messages = new ArrayList<>();
        for (Message message : messageRepository.values()) {
            if (message.getChannel().getName().equals(channel.getName())) {
                messages.add(message);
            }
        }
        return messages;
    }


    @Override
    public Message updateMessageContent(Message findMessage, String newContent) {
        if (newContent.isEmpty()) {
            throw new ServiceException(ErrorCode.EMPTY_CONTENT);
        }
        findMessage.update(newContent);
        return findMessage;
    }

    @Override
    public void removeMessageByWriter(User writer, UUID uuid) { // 작성자가 작성한 메시지 삭제하기
        if (!messageRepository.containsKey(uuid)) {
            throw new ServiceException(ErrorCode.CANNOT_FOUND_MESSAGE);
        }
        validateUser(writer);
        Message findMessage = messageRepository.get(uuid);
        messageRepository.remove(uuid);
    }


    @Override
    public void deleteMessageWithChannel(Channel channel) {
        getMessageByChannel(channel).stream()
                .map(Message::getId)
                .forEach(messageRepository::remove);
    }

    private void validateUser(User user) {
        User findUser = userService.getUserByPhone(user.getPhone())
                .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USER));
    }

    private void validateChannel(Channel channel) {
        Channel findChannel = channelService.getChannelByName(channel.getName())
                .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_CHANNEL));
    }
}
