package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.util.FileIOUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.sprint.mission.discodeit.util.FileIOUtil.saveToFile;

public class FileUserRepository implements UserRepository {
    private final Path filePath;
    private final Map<UUID, User> userMap;

    public FileUserRepository(String filePath) {
        this.filePath = Paths.get(filePath);
        if (!Files.exists(this.filePath)) {
            try {
                Files.createFile(this.filePath);
                saveToFile(new HashMap<>(), this.filePath);
            } catch (IOException e) {
                throw new RuntimeException("회원 파일을 초기화 하던 중에 문제가 발생했습니다", e);
            }
        }
        this.userMap = FileIOUtil.loadFromFile(this.filePath);
    }

    @Override
    public User save(User user) {
        userMap.put(user.getId(), user);
        FileIOUtil.saveToFile(userMap, filePath);
        return user;
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return userMap.values().stream()
                .filter(user -> user.getPhone().equals(phone))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return userMap.values().stream()
                .collect(Collectors.toList());
    }

    @Override
    public User delete(User user) {
        userMap.remove(user.getId());
        FileIOUtil.saveToFile(userMap, filePath);
        return user;
    }
}
