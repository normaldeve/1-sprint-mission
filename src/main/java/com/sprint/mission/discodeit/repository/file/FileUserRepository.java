package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.util.ValidPass;
import com.sprint.mission.discodeit.util.ValidPhone;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.sprint.mission.discodeit.error.UserError.*;
import static com.sprint.mission.discodeit.util.FileIOUtil.loadFromFile;
import static com.sprint.mission.discodeit.util.FileIOUtil.saveToFile;

public class FileUserRepository implements UserRepository {
    private final Path filePath;

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
    }

    @Override
    public User create(String name, String phone, String password) {
        Map<UUID, User> users = loadFromFile(filePath);
        if (!ValidPass.isValidPassword(password)) {
            throw new IllegalArgumentException(INVALID_PASSWORD.getMessage());
        }

        if (!ValidPhone.isValidPhone(phone)) {
            throw new IllegalArgumentException(INVALID_PHONE.getMessage());
        }
        if (users.values().stream()
                .anyMatch(user -> user.getPhone().equals(phone))) {
            throw new IllegalArgumentException(DUPLICATE_PHONE.getMessage());
        }

        User createUser = new User(name, phone, password);
        users.put(createUser.getId(), createUser);
        saveToFile(users, filePath);
        return createUser;
    }
}
