package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.error.ErrorCode;
import com.sprint.mission.discodeit.exception.ServiceException;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.FileIOUtil;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.sprint.mission.discodeit.util.FileIOUtil.saveToFile;

public class FileMessageService implements MessageService {
    private final Path filePath = Path.of("./result/messages.ser");
    Map<UUID, Message> messages = FileIOUtil.loadFromFile(filePath);
    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(UserService userService, ChannelService channelService) {
        if (!Files.exists(this.filePath)) {
            try {
                Files.createFile(this.filePath);
                saveToFile(new HashMap<>(), this.filePath);
            } catch (IOException e) {
                throw new RuntimeException("메시지 파일을 초기화 하던 중에 문제가 발생했습니다", e);
            }
        }
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message createMessage(String content, User writer, Channel channel) {
        if (content.isEmpty()) {
            throw new ServiceException(ErrorCode.EMPTY_CONTENT);
        }
        validateUser(writer);

        validateChannel(channel);

        Message message = new Message(content, writer, channel);
        messages.put(message.getId(), message);
        FileIOUtil.saveToFile(messages, filePath);
        return message;
    }

    @Override
    public List<Message> getMessageByUser(User writer) {
        validateUser(writer);

        List<Message> list = new ArrayList<>();
        for (Message message : messages.values()) {
            if (message.getWriter().getPhone().equals(writer.getPhone())) {
                list.add(message);
            }
        }
        return list;
    }

    @Override
    public List<Message> getMessageByChannel(Channel channel) {
        List<Message> list = new ArrayList<>();
        for (Message message : messages.values()) {
            if (message.getChannel().getName().equals(channel.getName())) {
                list.add(message);
            }
        }
        return list;
    }

    @Override
    public Message updateMessageContent(Message updateMessage, String newContent) {
        if (newContent.isEmpty()) {
            throw new ServiceException(ErrorCode.EMPTY_CONTENT);
        }
        updateMessage.update(newContent);
        messages.put(updateMessage.getId(), updateMessage);
        saveToFile(messages, filePath);
        return updateMessage;
    }

    @Override
    public void removeMessageByWriter(User writer, UUID uuid) {
        if (!messages.containsKey(uuid)) {
            throw new ServiceException(ErrorCode.CANNOT_FOUND_MESSAGE);
        }
        Message findMessage = messages.get(uuid);
        validateUser(writer);
        messages.remove(uuid);
    }

    @Override
    public void deleteMessageByChannel(Channel channel) {
        getMessageByChannel(channel).stream()
                .map(Message::getId)
                .forEach(messages::remove);

        FileIOUtil.saveToFile(messages, filePath);
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
