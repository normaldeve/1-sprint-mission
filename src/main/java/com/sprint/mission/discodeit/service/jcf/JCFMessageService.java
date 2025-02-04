package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.domain.Channel;
import com.sprint.mission.discodeit.domain.Message;
import com.sprint.mission.discodeit.domain.User;
import com.sprint.mission.discodeit.error.ErrorCode;
import com.sprint.mission.discodeit.exception.ServiceException;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messageRepository;
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.messageRepository = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message create(String content, User writer, Channel channel) throws IllegalArgumentException {
        if (content.isEmpty()) {
            throw new ServiceException(ErrorCode.EMPTY_CONTENT);
        }

        validateUser(writer);

        validateChannel(channel);

        Message message = new Message(content, writer, channel);
        messageRepository.put(message.getId(), message);
        return message;
    }

    @Override
    public Optional<Message> getMessage(UUID messageID) {
        return Optional.ofNullable(messageRepository.get(messageID));
    }

    @Override
    public List<Message> getMessageWithWriter(User writer) {
        validateUser(writer);
        return messageRepository.values().stream()
                .filter(message -> message.getWriter().equals(writer))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> getMessageWithChannel(Channel channel) {
        validateChannel(channel);
        return messageRepository.values().stream()
                .filter(message -> message.getChannel().equals(channel))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> getAllMessage() {
        return new ArrayList<>(messageRepository.values());
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
    public void deleteMessage(UUID messageID) {
        messageRepository.remove(messageID);
    }

    private void validateUser(User user) { // User가 repository에 저장되어 있는지 확인
        User findUser = userService.getUserByPhone(user.getPhone())
                .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USER));
    }

    private void validateChannel(Channel channel) { // Channel이 repository에 저장되어 있는지 확인
        Channel findChannel = channelService.getChannelByName(channel.getName())
                .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_CHANNEL));
    }
}
