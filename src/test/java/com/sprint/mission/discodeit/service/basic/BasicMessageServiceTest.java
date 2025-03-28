package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ServiceException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class BasicMessageServiceTest {

  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private MessageRepository messageRepository;
  @Mock
  private MessageMapper messageMapper;
  @Mock
  private BinaryContentStorage binaryContentStorage;
  @Mock
  private BinaryContentRepository binaryContentRepository;
  @Mock
  private PageResponseMapper pageResponseMapper;
  @InjectMocks
  private BasicMessageService messageService;

  @Test
  @DisplayName("성공 - 메시지 생성")
  void createMessage_success() {
    UUID channelId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    String content = "Hello World";

    MessageCreateRequest request = new MessageCreateRequest(content, channelId, userId);
    BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
        "file.txt", "text/plain", new byte[]{1, 2});
    Channel channel = new Channel(ChannelType.PUBLIC, "공개 채널", "공개 채널입니다.");
    User user = new User("user", "user@naver.com", "Rlawnsdn12!", null);
    UserDto userDto = new UserDto(UUID.randomUUID(), user.getUsername(), user.getEmail(), null,
        true);
    Message message = new Message(content, channel, user, List.of());
    MessageDto messageDto = new MessageDto(UUID.randomUUID(), Instant.now(), Instant.now(), content,
        channelId, userDto, null);

    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(messageRepository.save(any())).willReturn(message);
    given(messageMapper.toDto(any())).willReturn(messageDto);

    MessageDto result = messageService.create(request, List.of(binaryContentCreateRequest));

    assertThat(result.content()).isEqualTo("Hello World");
    then(messageRepository).should().save(any());
  }

  @Test
  @DisplayName("실패 - 채널이 존재하지 않음")
  void createMessage_fail_channelNotFound() {
    UUID channelId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("Hello World", channelId, userId);

    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.create(request, List.of()))
        .isInstanceOf(ServiceException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CANNOT_FOUND_CHANNEL);
  }

  @Test
  @DisplayName("성공 - 메시지 수정")
  void updateMessage_success() {
    UUID messageId = UUID.randomUUID();
    Message message = mock(Message.class);
    MessageUpdateRequest request = new MessageUpdateRequest("New Content");
    MessageDto messageDto = new MessageDto(messageId, Instant.now(), Instant.now(),
        request.newContent(), UUID.randomUUID(), null, null);

    given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
    given(messageMapper.toDto(any())).willReturn(messageDto);

    MessageDto update = messageService.update(messageId, request);

    assertThat(update.content()).isEqualTo("New Content");
    then(message).should().update(any());
  }

  @Test
  @DisplayName("실패 - 메시지 없음")
  void updateMessage_fail_notFound() {
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("New Content");

    given(messageRepository.findById(messageId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.update(messageId, request))
        .isInstanceOf(ServiceException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CANNOT_FOUND_MESSAGE);
  }

  @Test
  @DisplayName("성공 - 메시지 삭제")
  void deleteMessage_success() {
    UUID messageId = UUID.randomUUID();
    given(messageRepository.existsById(messageId)).willReturn(true);

    messageService.delete(messageId);

    then(messageRepository).should().deleteById(any());
  }

  @Test
  @DisplayName("실패 - 삭제하려는 메시지가 없음")
  void deleteMessage_fail_notFound() {
    UUID messageId = UUID.randomUUID();
    given(messageRepository.existsById(messageId)).willReturn(false);

    assertThatThrownBy(() -> messageService.delete(messageId))
        .isInstanceOf(ServiceException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CANNOT_FOUND_MESSAGE);
  }

  @Test
  @DisplayName("성공 - 채널 메시지 목록 조회")
  void findAllByChannelId_success() {
    UUID channelId = UUID.randomUUID();
    Instant cursor = Instant.now();
    Pageable pageable = PageRequest.of(0, 10);
    Message message = mock(Message.class);
    MessageDto dto = mock(MessageDto.class);

    Slice<Message> slice = new SliceImpl<>(List.of(message), pageable, false);
    Slice<MessageDto> dtoSlice = new SliceImpl<>(List.of(dto), pageable, false);
    PageResponse<MessageDto> response = new PageResponse<>(List.of(dto), null, 1, false, 0L);

    given(messageRepository.findAllByChannelIdWithAuthor(eq(channelId), any(), eq(pageable))).willReturn(slice);
    given(messageMapper.toDto(any())).willReturn(dto);
    given(pageResponseMapper.fromSlice(ArgumentMatchers.<Slice<MessageDto>>any(), any())).willReturn(response);

    PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, cursor, pageable);

    assertThat(result.content()).isNotNull();
  }
}
