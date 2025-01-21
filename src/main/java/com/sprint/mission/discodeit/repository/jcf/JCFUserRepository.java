package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.error.ErrorCode;
import com.sprint.mission.discodeit.exception.ServiceException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.util.ValidPass;
import com.sprint.mission.discodeit.util.ValidPhone;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> userMap;

    public JCFUserRepository() {
        this.userMap = new HashMap<>();
    }

    @Override
    public User create(String name, String phone, String password) {
        if (!ValidPass.isValidPassword(password)) {
            throw new ServiceException(ErrorCode.INVALID_PASSWORD);
        }

        if (!ValidPhone.isValidPhone(phone)) {
            throw new ServiceException(ErrorCode.INVALID_WRITER);
        }

        if (userMap.values().stream()
                .anyMatch(user -> user.getPhone().equals(phone))) {
            throw new ServiceException(ErrorCode.CANNOT_FOUND_USER);
        }
        User user = new User(name, phone, password);
        userMap.put(user.getId(), user);
        return user;
    }
}
