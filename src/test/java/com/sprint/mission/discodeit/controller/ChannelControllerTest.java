package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(ChannelController.class)
class ChannelControllerTest {

  @Autowired MockMvc mockMvc;

  @MockitoBean ChannelService channelService;

  @Test
  @DisplayName("공개 채널 생성 성공")
  void createPublicChannel_success() throws Exception {
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("test", "desc");
    ChannelDto channelDto = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "test", "desc", null, null);

    when(channelService.create(any(PublicChannelCreateRequest.class))).thenReturn(channelDto);

    mockMvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
              {
                "name": "test",
                "description": "desc"
              }
            """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("test"))
        .andExpect(jsonPath("$.description").value("desc"))
        .andExpect(jsonPath("$.type").value("PUBLIC"));
  }

  @Test
  @DisplayName("개인 채널 생성 성공")
  void createPrivateChannel_success() throws Exception {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(id1, id2));
    ChannelDto channelDto = new ChannelDto(UUID.randomUUID(), ChannelType.PRIVATE, null, null, null, null);

    when(channelService.create(any(PrivateChannelCreateRequest.class))).thenReturn(channelDto);

    mockMvc.perform(post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(String.format("""
              {
                "participantIds": ["%s", "%s"]
              }
            """, id1, id2)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.type").value("PRIVATE"));
  }

  @Test
  @DisplayName("채널 업데이트 성공")
  void updateChannel_success() throws Exception {
    UUID channelId = UUID.randomUUID();
    ChannelDto updated = new ChannelDto(channelId, ChannelType.PUBLIC, "new name", "new desc", null, null);
    when(channelService.update(eq(channelId), any())).thenReturn(updated);

    mockMvc.perform(patch("/api/channels/{channelId}", channelId)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
              {
                "newName": "new name",
                "newDescription": "new desc"
              }
            """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("new name"))
        .andExpect(jsonPath("$.description").value("new desc"));
  }

  @Test
  @DisplayName("채널 삭제 성공")
  void deleteChannel_success() throws Exception {
    UUID channelId = UUID.randomUUID();

    mockMvc.perform(delete("/api/channels/{channelId}", channelId))
        .andExpect(status().isNoContent());

    verify(channelService).delete(channelId);
  }

  @Test
  @DisplayName("사용자 참여 채널 목록 조회 성공")
  void findChannelsByUserId_success() throws Exception {
    UUID userId = UUID.randomUUID();
    ChannelDto channel1 = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "one", "desc1", null, null);
    ChannelDto channel2 = new ChannelDto(UUID.randomUUID(), ChannelType.PRIVATE, null, null, null, null);

    when(channelService.findAllByUserId(userId)).thenReturn(List.of(channel1, channel2));

    mockMvc.perform(get("/api/channels")
            .param("userId", userId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("one"))
        .andExpect(jsonPath("$[0].type").value("PUBLIC"))
        .andExpect(jsonPath("$[1].type").value("PRIVATE"));
  }
}
