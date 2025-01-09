package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JCFUserSerivce implements UserService {
    private final Map<UUID, User> userRepository;

    public JCFUserSerivce() {
        this.userRepository = new HashMap<>();
    }

    @Override
    public void createUser(User user) {
        if (userRepository.get(user.getId()) == null) {
            userRepository.put(user.getId(), user);
            System.out.println("사용자 생성이 완료되었습니다.");
        } else {
            System.out.println("이미 존재하는 사용자입니다.");
        }
    }

    @Override
    public User getUserById(UUID id) {
        if (userRepository.get(id) != null) {
            return userRepository.get(id);
        } else {
            System.out.println("유효하지 않는 사용자 ID이거나 존재하지 않는 사용자입니다");
            return null;
        }
    }

    @Override
    public User updateUserPasswordById(UUID id, String newPass) {
        if (userRepository.get(id) != null) {
            User user = getUserById(id);
            user.update(newPass);
            return user;
        } else {
            System.out.println("존재하지 않는 사용자입니다");
            return null;
        }
    }

    @Override
    public void deleteUser(User user) {
        if (userRepository.get(user.getId()) != null) {
            userRepository.remove(user.getId());
            System.out.println("사용자 삭제가 완료되었습니다.");
        } else {
            System.out.println("존재하지 않는 사용자입니다");
        }
    }
}
