package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.FileIOUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.sprint.mission.discodeit.error.ChannelError.CANNOT_FOUND_CHANNEL;
import static com.sprint.mission.discodeit.error.MessageError.EMPTY_CONTENT;
import static com.sprint.mission.discodeit.error.UserError.CANNOT_FOUND_USER;
import static com.sprint.mission.discodeit.util.FileIOUtil.saveToFile;

public class FileMessageRepository implements MessageRepository {
    private final Path filePath;

    public FileMessageRepository(String filePath) {
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
    public Message create(String content, User writer, Channel channel) {
        Map<UUID, Message> messages = FileIOUtil.loadFromFile(filePath);
        if (content.isEmpty()) {
            throw new IllegalArgumentException(EMPTY_CONTENT.getMessage());
        }

        Message message = new Message(content, writer, channel);
        messages.put(message.getId(), message);
        FileIOUtil.saveToFile(messages, filePath);
        return message;
    }
}
