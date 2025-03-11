package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.service.MessageService;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
//
//  private final MessageService messageService;
//
//  @PostMapping
//  public ResponseEntity<Message> createMessage(
//      @RequestPart("createMessageRequest") CreateMessageRequest createMessageRequest,
//      @RequestPart("file")
//      MultipartFile file) {
//    Optional<CreateBinaryContentRequest> fileRequest = Optional.empty();
//    if (file != null && !file.isEmpty()) {
//      try {
//        fileRequest = Optional.of(new CreateBinaryContentRequest(
//            file.getOriginalFilename(),
//            file.getContentType(),
//            file.getBytes()
//        ));
//      } catch (IOException e) {
//        throw new RuntimeException(e);
//      }
//    }
//
//    Message message = messageService.create(createMessageRequest, fileRequest);
//    return ResponseEntity.ok(message);
//  }
//
//  @GetMapping
//  public ResponseEntity<List<Message>> getAllByChannelId(
//      @RequestParam("channelId") UUID channelId) {
//    List<Message> messages = messageService.findAllByChannelId(channelId);
//    return ResponseEntity.ok(messages);
//  }
//
//  @PatchMapping("/{messageId}") // 첨부자료를 더 올리거나, 내용을 수정하고 싶을 때
//  public ResponseEntity<Message> update(@PathVariable("messageId") UUID messageId,
//      @RequestPart("messageRequest") UpdateMessageRequest messageRequest,
//      @RequestPart("file") MultipartFile file) {
//    Optional<CreateBinaryContentRequest> fileRequest = Optional.empty();
//    if (file != null && !file.isEmpty()) {
//      try {
//        fileRequest = Optional.of(new CreateBinaryContentRequest(
//            file.getOriginalFilename(),
//            file.getContentType(),
//            file.getBytes()
//        ));
//      } catch (IOException e) {
//        throw new RuntimeException(e);
//      }
//    }
//    Message message = messageService.updateMessageContent(messageId, messageRequest, fileRequest);
//    return ResponseEntity.ok(message);
//  }
//
//  @DeleteMapping("/{messageId}")
//  public ResponseEntity<String> delete(@PathVariable("messageId") UUID messageId) {
//    Message message = messageService.deleteMessage(messageId);
//    return ResponseEntity.ok(
//        "메시지 ID: " + message.getId() +
//            " 삭제가 완료되었습니다."
//    );
//  }
}
