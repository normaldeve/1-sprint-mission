package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.FileIOUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.sprint.mission.discodeit.error.ChannelError.*;
import static com.sprint.mission.discodeit.error.MessageError.*;
import static com.sprint.mission.discodeit.error.UserError.*;
import static com.sprint.mission.discodeit.util.FileIOUtil.saveToFile;

public class FileMessageService implements MessageService {
    private final Path filePath;
    private UserService userService;
    private ChannelService channelService;

    public FileMessageService(String filePath) {
        this.filePath = Paths.get(filePath);
        if (!Files.exists(this.filePath)) {
            try {
                Files.createFile(this.filePath);
                saveToFile(new HashMap<>(), this.filePath);
            } catch (IOException e) {
                throw new RuntimeException("메시지 파일을 초기화 하던 중에 문제가 발생했습니다", e);
            }
        }
    }

    @Override
    public void setDependency(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message createMessage(String content, User writer, Channel channel) {
        Map<UUID, Message> messages = FileIOUtil.loadFromFile(filePath);
        if (content.isEmpty()) {
            throw new IllegalArgumentException(EMPTY_CONTENT.getMessage());
        }
        if (!userService.userExists(writer.getPhone())) {
            throw new IllegalArgumentException(CANNOT_FOUND_USER.getMessage());
        }

        if (!channelService.channelExist(channel.getName())) {
            throw new IllegalArgumentException(CANNOT_FOUND_CHANNEL.getMessage());
        }
        Message message = new Message(content, writer, channel);
        messages.put(message.getId(), message);
        FileIOUtil.saveToFile(messages, filePath);
        return message;
    }

    @Override
    public List<Message> getMessageByUser(User writer) {
        Map<UUID, Message> messages = FileIOUtil.loadFromFile(filePath);
        if (!userService.userExists(writer.getPhone())) {
            throw new IllegalArgumentException(CANNOT_FOUND_USER.getMessage());
        }
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
        Map<UUID, Message> messages = FileIOUtil.loadFromFile(filePath);
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
        Map<UUID, Message> messages = FileIOUtil.loadFromFile(filePath);
        if (newContent.isEmpty()) {
            throw new IllegalArgumentException(EMPTY_CONTENT.getMessage());
        }
        updateMessage.update(newContent);
        messages.put(updateMessage.getId(), updateMessage);
        saveToFile(messages, filePath);
        return updateMessage;
    }

    @Override
    public void removeMessageByWriter(User writer, UUID uuid) {
        Map<UUID, Message> messages = FileIOUtil.loadFromFile(filePath);
        if (!messages.containsKey(uuid)) {
            throw new IllegalArgumentException(CANNOT_FOUND_MESSAGE.getMessage());
        }
        Message findMessage = messages.get(uuid);
        if (!findMessage.getWriter().getId().equals(writer.getId())) {
            throw new IllegalArgumentException(INVALID_WRITER.getMessage());
        }
        messages.remove(uuid);
    }

    @Override
    public void deleteMessageWithChannel(Channel channel) {
        Map<UUID, Message> messages = FileIOUtil.loadFromFile(filePath);
        getMessageByChannel(channel).stream()
                .map(Message::getId)
                .forEach(messages::remove);

        FileIOUtil.saveToFile(messages, filePath);
    }
}
