package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

import static com.sprint.mission.discodeit.error.UserError.*;

public class JCFUserSerivce implements UserService {
    private static final UserService instance = new JCFUserSerivce();
    private final Map<UUID, User> userRepository;
    private final MessageService messageService;
    private final ChannelService channelService;

    private JCFUserSerivce() {
        this.userRepository = new HashMap<>();
        this.messageService = JCFMessageService.getInstance();
        this.channelService = JCFChannelService.getInstance();
    }

    public static UserService getInstance() {
        return instance;
    }

    @Override
    public User createUser(String name, String phone, String password) throws IllegalArgumentException {
        if (!User.isValidPassword(password)) {
            throw new IllegalArgumentException(INVALID_PASSWORD.getMessage());
        }

        if (!User.isValidPhone(phone)) {
            throw new IllegalArgumentException(INVALID_PHONE.getMessage());
        }

        for (User user : userRepository.values()) {
            if (user.getPhone().equals(phone)) {
                throw new IllegalArgumentException(DUPLICATE_PHONE.getMessage());
            }
        }
        User createUser = new User(name, phone, password);
        userRepository.put(createUser.getId(), createUser);
        return createUser;
    }

    @Override
    public User getUserById(String phone) {
        for (User user : userRepository.values()) {
            if (user.getPhone() == phone) {
                return user;
            }
        }
        throw new IllegalArgumentException(CANNOTFOUND_PHONE.getMessage());
    }

    @Override
    public List<User> getAllUser() {
        if (userRepository.values() == null) {
            throw new IllegalArgumentException(EMPTY_USER.getMessage());
        }

        return new ArrayList<>(userRepository.values());
    }

    @Override
    public User updateUserPassword(String phone, String newPass) {
        for (User user : userRepository.values()) {
            if (user.getPhone() == phone) {
                user.update(newPass);
                return user;
            }
        }
        throw new IllegalArgumentException(CANNOTFOUND_PHONE.getMessage());
    }

    @Override
    public boolean deleteUser(String phone) { // 유저 정보 삭제 시 유저가 보낸 메시지와 채널 모두 조회가 되면 안된다.
        for (User user : userRepository.values()) {
            if (user.getPhone() == phone) {
                userRepository.remove(user.getId());
                System.out.println(user.getName() + "님의 정보가 삭제되었습니다");
                messageService.deleteAllMessageByWriter(user);
                for (Channel channel : channelService.getChannelsByUserId(user)) {
                    channel.removeUser(user);
                }
                return true;
            }
        }
        throw new IllegalArgumentException(CANNOTFOUND_USER.getMessage());
    }
}
