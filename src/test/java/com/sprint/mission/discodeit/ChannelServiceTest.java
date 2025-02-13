package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.domain.Channel;
import com.sprint.mission.discodeit.domain.PublicChannel;
import com.sprint.mission.discodeit.domain.User;
import com.sprint.mission.discodeit.dto.channel.CreateChannel;
import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserDTO;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.FileIOUtil;
import com.sprint.mission.discodeit.util.type.ChannelFormat;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@SpringBootTest
public class ChannelServiceTest {
    @Autowired
    private ChannelService channelService;

    @Autowired
    private UserService userService;

    private List<UUID> joinUser;

    @BeforeEach
    void before() {
        FileIOUtil.initializeFiles();
        CreateUserRequest request1 = new CreateUserRequest("테스트유저1", "010-1111-1111", "Abcdefg1234!!", null);
        CreateUserRequest request2 = new CreateUserRequest("테스트유저2", "010-2222-1111", "Abcdefg1234!!", null);
        CreateUserRequest request3 = new CreateUserRequest("테스트유저3", "010-3333-1111", "Abcdefg1234!!", null);
        CreateUserRequest request4 = new CreateUserRequest("테스트유저4", "010-4444-1111", "Abcdefg1234!!", null);
        UserDTO testUser1DTO = userService.create(request1);
        UserDTO testUser2DTO = userService.create(request2);
        UserDTO testUser3DTO = userService.create(request3);
        UserDTO testUser4DTO = userService.create(request4);
        joinUser = new ArrayList<>(Arrays.asList(testUser1DTO.getId(), testUser2DTO.getId(), testUser3DTO.getId(), testUser4DTO.getId()));

    }

    @AfterEach
    void after() {
        FileIOUtil.initializeFiles();
    }

    @DisplayName("Public 채널 테스트하기")
    @Test
    void createPublicChannel() {
        CreateChannel.PublicRequest request = new CreateChannel.PublicRequest("백엔드 개발자 오픈 채널", "지식 공유를 위해 만든 채널입니다.", ChannelFormat.TEXT, joinUser);
        PublicChannel publicChannel = (PublicChannel) channelService.createPublicChannel(request);

        Assertions.assertEquals(request.getName(), publicChannel.getName());
    }

    @DisplayName("Private 채널 테스트하기")
    @Test
    void createPrivateChannel() {
        CreateChannel.PrivateRequest request = new CreateChannel.PrivateRequest(joinUser, ChannelFormat.TEXT);
        Channel privateChannel = channelService.createPrivateChannel(request);

        Assertions.assertNotNull(privateChannel);
    }
}
