package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ServiceException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final BinaryContentRepository binaryContentRepository;

  @Override
  public Message create(CreateMessageRequest messageRequest,
      Optional<CreateBinaryContentRequest> binaryContentRequest) {
    if (messageRequest.content().isEmpty()) {
      throw new ServiceException(ErrorCode.EMPTY_CONTENT);
    }

    // 작성자와 채널에 대한 검증
    validUser(messageRequest.userId());
    validChannel(messageRequest.channelId());

    BinaryContent file = null;
    if (binaryContentRequest.isPresent()) {
      CreateBinaryContentRequest contentRequest = binaryContentRequest.get();

      file = new BinaryContent(contentRequest.bytes(), contentRequest.contentType(),
          contentRequest.fileName());

      binaryContentRepository.save(file);
    }

    Message message = new Message(messageRequest.content(), messageRequest.userId(),
        messageRequest.channelId());
    message.getAttachmentsID().add(file.getId());
    messageRepository.save(message);
    return message;
  }

  @Override
  public List<Message> findAllByChannelId(UUID channelID) {
    validChannel(channelID);

    return messageRepository.findByChannelId(channelID);
  }

  @Override
  public List<Message> getAllMessage() {
    return messageRepository.findAll();
  }
  
  @Override
  public Message updateMessageContent(UUID messageId, UpdateMessageRequest request,
      Optional<CreateBinaryContentRequest> binaryContentRequest) {
    validMessage(messageId);

    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_MESSAGE));

    BinaryContent file = null;
    if (binaryContentRequest.isPresent()) {
      CreateBinaryContentRequest contentRequest = binaryContentRequest.get();

      file = new BinaryContent(contentRequest.bytes(), contentRequest.contentType(),
          contentRequest.fileName());

      binaryContentRepository.save(file);
    }

    message.update(request.newContent(), file.getId());

    return messageRepository.save(message);
  }


  @Override
  public Message deleteMessage(UUID messageID) {
    Message deleteMessage = messageRepository.findById(messageID)
        .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_MESSAGE));

    List<UUID> attachmentsID = deleteMessage.getAttachmentsID();
    if (attachmentsID != null && !attachmentsID.isEmpty()) {
      attachmentsID.forEach(id -> {
        binaryContentRepository.findById(id)
            .ifPresent(binaryContent -> binaryContentRepository.deleteById(id));
      });
    }

    Message delete = messageRepository.delete(deleteMessage);
    return delete;
  }

  private void validUser(UUID userId) {
    if (!userRepository.existsById(userId)) {
      throw new ServiceException(ErrorCode.CANNOT_FOUND_USER);
    }
  }

  private void validChannel(UUID channelId) {
    if (!channelRepository.channelExistById(channelId)) {
      throw new ServiceException(ErrorCode.CANNOT_FOUND_CHANNEL);
    }
  }

  private void validMessage(UUID messageId) {
    messageRepository.findById(messageId)
        .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_MESSAGE));
  }
}