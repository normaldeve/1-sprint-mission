package com.sprint.mission.discodeit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class IntegrationTests {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ChannelRepository channelRepository;
  @Autowired
  private MessageRepository messageRepository;

  @Test
  @DisplayName("사용자 생성 및 조회")
  void userCreateAndFetch() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest("testuser", "test@email.com", "Password1!");
    byte[] jsonBytes = objectMapper.writeValueAsBytes(request);

    MockMultipartFile userCreateRequest = new MockMultipartFile(
        "userCreateRequest",
        "userCreateRequest.json",
        "application/json",
        jsonBytes
    );

    // when
    ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders.multipart("/api/users")
                .file(userCreateRequest)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        )
        .andExpect(status().isCreated());

    // then
    String responseBody = result.andReturn().getResponse().getContentAsString();
    UserDto userDto = objectMapper.readValue(responseBody, UserDto.class);

    assertThat(userDto.username()).isEqualTo("testuser");
  }

  @Test
  @DisplayName("채널 생성 및 삭제")
  void channelCreateAndDelete() throws Exception {
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("test-channel", "desc");

    ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());

    String responseBody = result.andReturn().getResponse().getContentAsString();
    ChannelDto channelDto = objectMapper.readValue(responseBody, ChannelDto.class);

    UUID channelId = channelDto.id();

    mockMvc.perform(MockMvcRequestBuilders.delete("/api/channels/" + channelId))
        .andExpect(status().isNoContent());

    assertThat(channelRepository.findById(channelId)).isEmpty();
  }

  @Test
  @DisplayName("메시지 생성 및 수정")
  void messageCreateAndUpdate() throws Exception {
    // 사용자 생성
    UserCreateRequest userRequest = new UserCreateRequest("msguser", "msg@email.com", "Password1!");
    MockMultipartFile userPart = new MockMultipartFile(
        "userCreateRequest",
        "userCreateRequest.json",
        "application/json",
        objectMapper.writeValueAsBytes(userRequest)
    );

    String userResponse = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/users")
            .file(userPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    UserDto user = objectMapper.readValue(userResponse, UserDto.class);

    // 채널 생성
    PublicChannelCreateRequest channelRequest = new PublicChannelCreateRequest("msg-channel", "msg-desc");
    String channelResponse = mockMvc.perform(MockMvcRequestBuilders.post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(channelRequest)))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    ChannelDto channel = objectMapper.readValue(channelResponse, ChannelDto.class);

    // 메시지 생성
    MessageCreateRequest request = new MessageCreateRequest("test", channel.id(), user.id());
    MockMultipartFile messagePart = new MockMultipartFile(
        "messageCreateRequest",
        "messageCreateRequest.json",
        "application/json",
        objectMapper.writeValueAsBytes(request)
    );

    String messageResponse = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/messages")
            .file(messagePart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    MessageDto createdMessage = objectMapper.readValue(messageResponse, MessageDto.class);
    UUID messageId = createdMessage.id();

    // 메시지 수정
    MessageUpdateRequest updateRequest = new MessageUpdateRequest("Updated content");

    mockMvc.perform(MockMvcRequestBuilders.patch("/api/messages/" + messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").value("Updated content"));
  }
}
