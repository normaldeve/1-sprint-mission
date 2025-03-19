package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageDTO;
import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

  private final MessageService messageService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageDTO> create(
      @RequestPart("createMessageRequest") CreateMessageRequest createMessageRequest,
      @RequestPart("file")
      List<MultipartFile> attachments) {
    List<CreateBinaryContentRequest> attachmentRequests = Optional.ofNullable(attachments)
        .map(files -> files.stream()
            .map(file -> {
              try {
                return new CreateBinaryContentRequest(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes(),
                    file.getSize()
                );
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
            .toList())
        .orElse(new ArrayList<>());

    MessageDTO message = messageService.create(createMessageRequest, attachmentRequests);
    return ResponseEntity.ok(message);
  }

  @GetMapping
  public ResponseEntity<PageResponse<MessageDTO>> getAllByChannelId(
      @RequestParam("channelId") UUID channelId,
      @RequestParam(value = "page", defaultValue = "0") int page) {
    PageResponse<MessageDTO> messages = messageService.findAllByChannelId(channelId, page);
    return ResponseEntity.ok(messages);
  }

  @PatchMapping(value = "/{messageId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageDTO> update(@PathVariable("messageId") UUID messageId,
      @RequestPart("messageRequest") UpdateMessageRequest messageRequest) {
    MessageDTO message = messageService.update(messageId, messageRequest);
    return ResponseEntity.ok(message);
  }

  @DeleteMapping("/{messageId}")
  public ResponseEntity<String> delete(@PathVariable("messageId") UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity.ok(
        "메시지 삭제가 완료되었습니다."
    );
  }
}
