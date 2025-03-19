package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserDTO;;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.ServiceException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class BasicUserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private BinaryContentRepository binaryContentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Test
    @DisplayName("회원 생성 테스트")
    @Transactional
    void createUserTest() {
        // given
        CreateUserRequest createUserRequest = new CreateUserRequest("testUser01", "testUser00@naver.com", "Alasdfasdew1234!!");
        CreateBinaryContentRequest createBinaryContentRequest = new CreateBinaryContentRequest(
                "example.txt",       // fileName
                "text/plain",         // contentType
                "Hello, world!".getBytes(), // bytes
                1024L                 // size
        );

        // when
        UserDTO userDTO = userService.create(createUserRequest, Optional.of(createBinaryContentRequest));

        // then
        User user = userRepository.findByUsername("testUser01").orElseThrow();
        BinaryContent binaryContent = binaryContentRepository.findById(user.getProfile().getId()).orElseThrow();
        UserStatus userStatus = userStatusRepository.findByUserId(user.getId()).orElseThrow();

        // 검증
        assertNotNull(user, "User should be saved");
        log.info("user : {}", user);
        assertNotNull(binaryContent, "BinaryContent should be saved");
        log.info("binaryContent : {}", binaryContent);
        assertNotNull(userStatus, "UserStatus should be saved");
        log.info("userStatus : {}", userStatus);
    }

    @Test
    @DisplayName("회원 조회 테스트")
    @Transactional
    void findUserTest() {
        // given
        CreateUserRequest createUserRequest = new CreateUserRequest("testUser02", "testUser02@naver.com", "Alasdfasdew1234!!");
        CreateBinaryContentRequest createBinaryContentRequest = new CreateBinaryContentRequest(
                "example2.txt",       // fileName
                "text/plain",          // contentType
                "Hello, world 2!".getBytes(), // bytes
                2048L                  // size
        );
        UserDTO createdUserDTO = userService.create(createUserRequest, Optional.of(createBinaryContentRequest));

        // when
        UserDTO foundUserDTO = userService.find(createdUserDTO.getId());

        // then
        assertNotNull(foundUserDTO, "User should be found");
        assertEquals(createdUserDTO.getId(), foundUserDTO.getId(), "User IDs should match");
    }

    @Test
    @DisplayName("모든 회원 조회 테스트")
    @Transactional
    void findAllUsersTest() {
        CreateUserRequest createUserRequest1 = new CreateUserRequest("testUser01", "testUser01@naver.com", "Password1234!");
        CreateUserRequest createUserRequest2 = new CreateUserRequest("testUser02", "testUser02@naver.com", "Password1234!");

        CreateBinaryContentRequest createBinaryContentRequest1 = new CreateBinaryContentRequest(
                "example1.txt", "text/plain", "Hello, world 1!".getBytes(), 1024L
        );
        CreateBinaryContentRequest createBinaryContentRequest2 = new CreateBinaryContentRequest(
                "example2.txt", "text/plain", "Hello, world 2!".getBytes(), 1024L
        );

        userService.create(createUserRequest1, Optional.of(createBinaryContentRequest1));
        userService.create(createUserRequest2, Optional.of(createBinaryContentRequest2));

        List<UserDTO> users = userService.findAll();

        assertTrue(users.size() >= 2, "There should be at least 2 users");
    }


    @Test
    @DisplayName("회원 삭제 테스트")
    @Transactional
    void deleteUserTest() {
        // given
        CreateUserRequest createUserRequest = new CreateUserRequest("testUser06", "testUser06@naver.com", "Alasdfasdew1234!!");
        CreateBinaryContentRequest createBinaryContentRequest = new CreateBinaryContentRequest(
                "example4.txt",       // fileName
                "text/plain",          // contentType
                "Hello, world 4!".getBytes(), // bytes
                4096L                  // size
        );
        UserDTO createdUserDTO = userService.create(createUserRequest, Optional.of(createBinaryContentRequest));

        // when
        userService.delete(createdUserDTO.getId());

        // then
        assertThrows(ServiceException.class, () -> userService.find(createdUserDTO.getId()), "User should be deleted and not found");
    }
}