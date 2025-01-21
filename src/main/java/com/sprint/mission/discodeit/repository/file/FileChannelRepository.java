package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.error.ErrorCode;
import com.sprint.mission.discodeit.exception.ServiceException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.util.FileIOUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.sprint.mission.discodeit.util.FileIOUtil.loadFromFile;
import static com.sprint.mission.discodeit.util.FileIOUtil.saveToFile;

public class FileChannelRepository implements ChannelRepository {
    private final Path filePath;
    private final Map<UUID, Channel> channelMap;

    public FileChannelRepository(String filePath) {
        this.filePath = Paths.get(filePath);
        if (!Files.exists(this.filePath)) {
            try {
                Files.createFile(this.filePath);
                saveToFile(new HashMap<>(), this.filePath);
            } catch (IOException e) {
                throw new RuntimeException("채널 파일을 초기화 하던 중에 문제가 발생했습니다", e);
            }
        }
        this.channelMap = FileIOUtil.loadFromFile(this.filePath);
    }
    @Override
    public Channel create(String name, User creator) {
        Map<UUID, Channel> channels = loadFromFile(filePath);
        if (channels.values().stream()
                .anyMatch(user -> user.getName().equals(name))) {
            throw new ServiceException(ErrorCode.DUPLICATE_CHANNEL);
        }

        Channel createChannel = new Channel(name, creator);
        channels.put(createChannel.getId(), createChannel);
        saveToFile(channels, filePath);
        return createChannel;
    }
}
