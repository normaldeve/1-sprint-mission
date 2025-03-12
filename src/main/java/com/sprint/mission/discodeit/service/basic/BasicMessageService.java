package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ServiceException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final ModelMapper modelMapper;

  @Transactional
  @Override
  public MessageDTO create(CreateMessageRequest messageRequest,
                           List<CreateBinaryContentRequest> binaryContentRequest) {
    UUID authorId = messageRequest.userId();
    UUID channelId = messageRequest.channelId();

    User user = validAuthor(authorId);
    Channel channel = validChannel(channelId);

    List<BinaryContent> attachments = binaryContentRequest.stream()
            .map(attachmentRequest -> {
              BinaryContent binaryContent = BinaryContent.builder()
                      .fileName(attachmentRequest.fileName())
                      .size(attachmentRequest.size())
                      .contentType(attachmentRequest.contentType())
                      .bytes(attachmentRequest.bytes())
                      .build();
              return binaryContentRepository.save(binaryContent);
            })
            .toList();

    Message message = Message.builder()
            .content(messageRequest.content())
            .channel(channel)
            .author(user)
            .attachments(attachments)
            .build();

    messageRepository.save(message);

    return modelMapper.map(message, MessageDTO.class);
  }

  @Transactional(readOnly = true)
  @Override
  public MessageDTO find(UUID messageId) {
    Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_MESSAGE));

    return modelMapper.map(message, MessageDTO.class);
  }

  @Transactional(readOnly = true)
  @Override
  public List<MessageDTO> findAllByChannelId(UUID channelID) {
    Channel channel = validChannel(channelID);

    List<Message> messages = messageRepository.findByChannelId(channelID);
    return messages.stream()
            .map(message -> modelMapper.map(message,MessageDTO.class))
            .toList();
  }

  @Transactional
  @Override
  public MessageDTO update(UUID messageId, UpdateMessageRequest request) {
    String newContent = request.newContent();

    Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_MESSAGE));

    message.update(newContent);

    return modelMapper.map(message, MessageDTO.class);
  }

  @Transactional
  @Override
  public void delete(UUID messageId) {

    Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_MESSAGE));

    messageRepository.deleteById(messageId);
  }

  private User validAuthor(UUID authorId) {
    if (!userRepository.existsById(authorId)) {
      throw new ServiceException(ErrorCode.CANNOT_FOUND_USER);
    }

    return userRepository.findById(authorId).orElse(null);
  }

  private Channel validChannel(UUID channelId) {
    if (!channelRepository.existsById(channelId)) {
      throw new ServiceException(ErrorCode.CANNOT_FOUND_CHANNEL);
    }
    return channelRepository.findById(channelId).orElse(null);
  }
}