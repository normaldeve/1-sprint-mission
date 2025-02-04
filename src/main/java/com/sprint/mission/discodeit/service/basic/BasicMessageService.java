package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.domain.Channel;
import com.sprint.mission.discodeit.domain.Message;
import com.sprint.mission.discodeit.domain.User;
import com.sprint.mission.discodeit.error.ErrorCode;
import com.sprint.mission.discodeit.exception.ServiceException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public BasicMessageService(MessageRepository messageRepository, UserRepository userRepository, ChannelRepository channelRepository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public Message create(String content, User writer, Channel channel) {
        if (content.isEmpty()) {
            throw new ServiceException(ErrorCode.EMPTY_CONTENT);
        }

        existUser(writer);
        existChannel(channel);
        Message message = new Message(content, writer, channel);
        messageRepository.save(message);
        return message;
    }

    @Override
    public Optional<Message> getMessage(UUID messageID) {
        return messageRepository.findById(messageID);
    }

    @Override
    public List<Message> getMessageWithWriter(User writer) {
        existUser(writer);
        List<Message> messageList = messageRepository.findAll();
        return messageList.stream()
                .filter(message -> message.getWriter().equals(writer))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> getMessageWithChannel(Channel channel) {
        existChannel(channel);
        List<Message> messageList = messageRepository.findAll();
        return messageList.stream()
                .filter(message -> message.getChannel().equals(channel))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> getAllMessage() {
        return messageRepository.findAll();
    }

    @Override
    public Message updateMessageContent(Message message, String newContent) {
        messageRepository.findById(message.getId())
                .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_MESSAGE));

        message.update(newContent);

        messageRepository.save(message);
        return message;
    }

    @Override
    public void deleteMessage(UUID messageID) {
        Optional<Message> deleteMessage = messageRepository.findById(messageID);
        messageRepository.delete(deleteMessage.orElse(null));
    }

    private void existUser(User user) {
        userRepository.findByPhone(user.getPhone())
                .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USER));
    }

    private void existChannel(Channel channel) {
        channelRepository.findByName(channel.getName())
                .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_CHANNEL));
    }
}