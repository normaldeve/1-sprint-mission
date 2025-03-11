package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Test
    void userSave() {
        User user = User.builder()
                .username("user00")
                .email("user00@gmail.com")
                .password("Abcdefgh1234!!")
                .profile(null)
                .build();
        userRepository.save(user);
    }
}