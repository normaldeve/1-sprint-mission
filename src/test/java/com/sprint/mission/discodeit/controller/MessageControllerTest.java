package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("dev")
@WebMvcTest(controllers = MessageController.class)
public class MessageControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private MessageService messageService;

  @Test
  @DisplayName("성공 - 메시지 생성")
  void createMessage_success() throws Exception {
    MessageCreateRequest request = new MessageCreateRequest("test", UUID.randomUUID(), UUID.randomUUID());
    MessageDto dto = new MessageDto(UUID.randomUUID(), Instant.now(), Instant.now(), "test",
        UUID.randomUUID(), null, null);

    BDDMockito.given(messageService.create(any(), any())).willReturn(dto);

    MockMultipartFile jsonPart = new MockMultipartFile("messageCreateRequest", null,
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(request));

    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/messages")
            .file(jsonPart))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.content").value("test"));
  }

  @Test
  @DisplayName("실패 - 메시지 업데이트 요청 시 존재하지 않는 메시지")
  void updateMessage_notFound() throws Exception {
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest updateRequest = new MessageUpdateRequest("Updated!");

    BDDMockito.given(messageService.update(any(), any()))
        .willThrow(new RuntimeException("Message not found"));

    mockMvc.perform(MockMvcRequestBuilders.patch("/api/messages/" + messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @DisplayName("성공 - 메시지 목록 조회")
  void findAllByChannelId_success() throws Exception {
    UUID channelId = UUID.randomUUID();
    MessageDto dto = new MessageDto(UUID.randomUUID(), Instant.now(), Instant.now(), "test",
        channelId, null, null);
    PageResponse<MessageDto> response = new PageResponse<>(List.of(dto), null, 1, false, 1L);

    BDDMockito.given(messageService.findAllByChannelId(any(), any(), any())).willReturn(response);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/messages")
            .param("channelId", channelId.toString())
            .param("page", "0")
            .param("size", "50"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].channelId").value(channelId.toString()));
  }
}
