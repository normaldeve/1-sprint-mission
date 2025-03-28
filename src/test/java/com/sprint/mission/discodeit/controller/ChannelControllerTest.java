package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChannelController.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class ChannelControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockitoBean
  ChannelService channelService;

  @Test
  @DisplayName("성공 - 공개 채널 생성")
  void createPublicChannel_success() throws Exception {
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("default", "default");
    ChannelDto channelDto = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, request.name(),
        request.description(), null, null);

    BDDMockito.given(channelService.create(any(PublicChannelCreateRequest.class)))
        .willReturn(channelDto);

    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("default"));
  }

  @Test
  @DisplayName("성공 - 채널 목록 조회")
  void findAllChannels_success() throws Exception {
    UUID userId = UUID.randomUUID();
    ChannelDto channelDto = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "default",
        "PUBLIC", null, null);

    BDDMockito.given(channelService.findAllByUserId(userId))
        .willReturn(List.of(channelDto));

    mockMvc.perform(get("/api/channels")
            .param("userId", userId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("default"));
  }
}
