package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.util.FileIOUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.sprint.mission.discodeit.util.FileIOUtil.saveToFile;

public class FileMessageRepository implements MessageRepository {
    private final Path filePath;
    private final Map<UUID, Message> messageMap;

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
        this.messageMap = FileIOUtil.loadFromFile(this.filePath);
    }

    @Override
    public Message save(Message message) {
        messageMap.put(message.getId(), message);
        FileIOUtil.saveToFile(messageMap, filePath);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID uuid) {

    }

    @Override
    public List<Message> findByUser(User user) {
    }

    @Override
    public List<Message> findByChannel(Channel channel) {
    }

    @Override
    public List<Message> findAll() {
    }

    @Override
    public Message delete(Message message) {
        return null;
    }
}
