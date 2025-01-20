package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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
                throw new RuntimeException("Error initializing user repository file", e);
            }
        }
    }

    @Override
    public void setDependency(UserService userService, ChannelService channelService) {

    }

    @Override
    public Message createMessage(String content, User writer, Channel channel) {
        return null;
    }

    @Override
    public List<Message> getMessageByUser(User writer) {
        return List.of();
    }

    @Override
    public List<Message> getMessageByChannel(Channel channel) {
        return List.of();
    }

    @Override
    public Message updateMessageContent(UUID id, String newContent) {
        return null;
    }

    @Override
    public void removeMessageByWriter(User writer, UUID uuid) {

    }

    @Override
    public void deleteMessageWithChannel(Channel channel) {

    }
}
