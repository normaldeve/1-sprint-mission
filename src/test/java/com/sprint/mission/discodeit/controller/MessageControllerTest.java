package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.springframework.test.context.ActiveProfiles;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.service.MessageService;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(MessageController.class)
class MessageControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private MessageService messageService;

  @Test
  @DisplayName("메시지 생성 요청 성공 - 첨부파일 없음")
  void createMessage_withoutAttachment_success() throws Exception {
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    UUID messageId = UUID.randomUUID();

    MockMultipartFile messageCreateRequest = new MockMultipartFile(
        "messageCreateRequest",
        "",
        "application/json",
        String.format("""
            {
              "channelId": "%s",
              "authorId": "%s",
              "content": "Hello world!"
            }
        """, channelId, authorId).getBytes(StandardCharsets.UTF_8)
    );

    MessageDto responseDto = new MessageDto(
        messageId,
        Instant.now(),
        Instant.now(),
        "Hello world!",
        channelId,
        new UserDto(authorId, "junwo", "junwo@email.com", null, null),
        null
    );

    when(messageService.create(any(), any())).thenReturn(responseDto);

    mockMvc.perform(multipart("/api/messages")
            .file(messageCreateRequest)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(messageId.toString()))
        .andExpect(jsonPath("$.content").value("Hello world!"))
        .andExpect(jsonPath("$.channelId").value(channelId.toString()))
        .andExpect(jsonPath("$.author.username").value("junwo"));
  }

  @Test
  @DisplayName("메시지 생성 성공 - 첨부파일 포함")
  void createMessage_withAttachment_success() throws Exception {
    UUID messageId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();

    MockMultipartFile messageCreateRequest = new MockMultipartFile(
        "messageCreateRequest",
        "",
        "application/json",
        ("""
        {
          "content": "Hello with file",
          "channelId": "%s",
          "authorId": "%s"
        }
        """.formatted(channelId.toString(), authorId.toString())
        ).getBytes(StandardCharsets.UTF_8)
    );

    MockMultipartFile attachmentFile = new MockMultipartFile(
        "attachments",
        "hello.png",
        "image/png",
        "fake image content".getBytes(StandardCharsets.UTF_8)
    );

    UserDto userDto = new UserDto(authorId, "junwo", "junwo@email.com", null, null);
    MessageDto messageDto = new MessageDto(messageId, Instant.now(), Instant.now(), "Hello with file", channelId, userDto, List.of());

    when(messageService.create(any(), any())).thenReturn(messageDto);

    mockMvc.perform(
            multipart("/api/messages")
                .file(messageCreateRequest)
                .file(attachmentFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(messageId.toString()))
        .andExpect(jsonPath("$.content").value("Hello with file"));
  }

  @Test
  @DisplayName("메시지 목록 조회 성공")
  void findAllByChannelId_success() throws Exception {
    UUID channelId = UUID.randomUUID();
    UUID messageId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();

    MessageDto messageDto = new MessageDto(
        messageId,
        Instant.now(),
        Instant.now(),
        "test message",
        channelId,
        new UserDto(authorId, "junwo", "junwo@email.com", null, null),
        null
    );

    PageResponse<MessageDto> pageResponse = new PageResponse<>(
        List.of(messageDto), null, 10, false, 0L
    );

    when(messageService.findAllByChannelId(eq(channelId), any(), any())).thenReturn(pageResponse);

    mockMvc.perform(get("/api/messages")
            .param("channelId", channelId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(messageId.toString()))
        .andExpect(jsonPath("$.content[0].content").value("test message"))
        .andExpect(jsonPath("$.content[0].author.username").value("junwo"));
  }

  @Test
  @DisplayName("메시지 업데이트 성공")
  void updateMessage_success() throws Exception {
    UUID messageId = UUID.randomUUID();

    MessageDto responseDto = new MessageDto(
        messageId,
        Instant.now(),
        Instant.now(),
        "updated content",
        UUID.randomUUID(),
        new UserDto(UUID.randomUUID(), "junwo", "junwo@email.com", null, null),
        null
    );

    when(messageService.update(eq(messageId), any())).thenReturn(responseDto);

    mockMvc.perform(patch("/api/messages/{messageId}", messageId)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
                {
                  "newContent": "updated content"
                }
            """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").value("updated content"));
  }

  @Test
  @DisplayName("메시지 삭제 성공")
  void deleteMessage_success() throws Exception {
    UUID messageId = UUID.randomUUID();

    mockMvc.perform(delete("/api/messages/{messageId}", messageId))
        .andExpect(status().isNoContent());

    verify(messageService).delete(messageId);
  }
}