package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.domain.BinaryContent;
import com.sprint.mission.discodeit.domain.Channel;
import com.sprint.mission.discodeit.domain.Message;
import com.sprint.mission.discodeit.domain.User;
import com.sprint.mission.discodeit.dto.message.CreateMessage;
import com.sprint.mission.discodeit.dto.message.UpdateMessage;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message create(CreateMessage.Request request) {
        if (request.getContent().isEmpty()) {
            throw new ServiceException(ErrorCode.EMPTY_CONTENT);
        }

        // 작성자와 채널에 대한 검증
        validUser(request.getWriter().getId());
        validChannel(request.getChannel().getId());

        // 해당 첨부자료가 레포지토리에 저장되어 있는지 확인
        for (UUID attachmentID : request.getAttachmentsID()) {
            Optional<BinaryContent> attachment = binaryContentRepository.findById(attachmentID);
            if (attachment.isEmpty()) {
                throw new ServiceException(ErrorCode.CANNOT_FOUND_ATTACHMENT);
            }
        }

        Message message = new Message(request.getContent(), request.getWriter().getId(), request.getChannel().getId(), request.getAttachmentsID());
        messageRepository.save(message);
        return message;
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelID) {
        validChannel(channelID);

        List<Message> messages = messageRepository.findAll();
        return messages.stream()
                .filter(message -> message.getChannelID().equals(channelID))
                .collect(Collectors.toList());

    }

    @Override
    public List<Message> getAllMessage() {
        return messageRepository.findAll();
    }

    @Override
    public Message updateMessageContent(UpdateMessage.Request request) {
        UUID messageId = request.getMessage().getId();
        validMessage(messageId);

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_MESSAGE));

        message.update(request.getNewContent(), request.getNewAttachment());

        return messageRepository.save(message);
    }


    @Override
    public void deleteMessage(UUID messageID) {
        Message deleteMessage = messageRepository.findById(messageID).orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_MESSAGE));
        messageRepository.delete(deleteMessage);
        List<UUID> attachmentsID = deleteMessage.getAttachmentsID();
        if (attachmentsID != null && !attachmentsID.isEmpty()) {
            attachmentsID.forEach(id -> {
                binaryContentRepository.findById(id)
                        .ifPresent(binaryContent -> binaryContentRepository.deleteById(id));
            });
        }

    }

    private void validUser(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USER));
    }

    private void validChannel(UUID channelId) {
        channelRepository.findById(channelId)
                .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_CHANNEL)));
    }

    private void validMessage(UUID messageId) {
        messageRepository.findById(messageId)
                .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_MESSAGE));
    }
}