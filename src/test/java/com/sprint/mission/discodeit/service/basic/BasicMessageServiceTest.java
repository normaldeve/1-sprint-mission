package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@ActiveProfiles("test")
@Import(BasicMessageService.class)
@ExtendWith(MockitoExtension.class)
public class BasicMessageServiceTest {
  @Mock
  private MessageRepository messageRepository;
  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private UserRepository userRepository;
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
  @DisplayName("메시지 생성 성공 - 첨부파일 없음")
  void createMessage_withoutAttachment() {
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    UUID messageId = UUID.randomUUID();

    MessageCreateRequest request = new MessageCreateRequest("Hello", channelId, authorId);
    Channel channel = new Channel(ChannelType.PUBLIC, "test", "desc");
    User user = new User("junwo", "email", "pass", null);
    ReflectionTestUtils.setField(user, "id", authorId);

    when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
    when(userRepository.findById(authorId)).thenReturn(Optional.of(user));

    UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getEmail(), null, null);
    Message message = new Message("Hello", channel, user, List.of());
    ReflectionTestUtils.setField(message, "id", messageId);
    when(messageRepository.save(any())).thenReturn(message);
    when(messageMapper.toDto(any())).thenReturn(
        new MessageDto(message.getId(), Instant.now(), Instant.now(), message.getContent(),
            message.getChannel().getId(), userDto, null));

    MessageDto result = messageService.create(request, List.of());

    assertThat(result).isNotNull();
    verify(binaryContentRepository, never()).save(any());
    verify(binaryContentStorage, never()).put(any(), any());
  }

  @Test
  @DisplayName("메시지 생성 성공 - 첨부파일 포함")
  void createMessage_withAttachment_success() {
    // given
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    UUID messageId = UUID.randomUUID();
    String content = "Hello with attachment";

    byte[] dummyBytes = "fake-image-bytes".getBytes();
    BinaryContentCreateRequest attachmentRequest = new BinaryContentCreateRequest(
        "image.png", "image/png", dummyBytes
    );

    MessageCreateRequest request = new MessageCreateRequest(content, channelId, authorId);
    Channel channel = new Channel(ChannelType.PUBLIC, "test", "desc");
    User author = new User("junwo", "email", "pass", null);
    ReflectionTestUtils.setField(author, "id", authorId);

    BinaryContent binaryContent = new BinaryContent("image.png", (long) dummyBytes.length, "image/png");
    UUID attachmentId = UUID.randomUUID();
    ReflectionTestUtils.setField(binaryContent, "id", attachmentId);

    Message message = new Message(content, channel, author, List.of(binaryContent));
    ReflectionTestUtils.setField(message, "id", messageId);

    UserDto userDto = new UserDto(author.getId(), author.getUsername(), author.getEmail(), null, null);

    // when
    when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
    when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
    when(binaryContentRepository.save(any())).thenReturn(binaryContent);
    when(messageRepository.save(any())).thenReturn(message);
    when(messageMapper.toDto(any())).thenReturn(
        new MessageDto(message.getId(), Instant.now(), Instant.now(), message.getContent(),
            message.getChannel().getId(), userDto, List.of())
    );

    MessageDto result = messageService.create(request, List.of(attachmentRequest));

    assertThat(result).isNotNull();
    verify(binaryContentRepository).save(any());
    verify(binaryContentStorage).put(any(), any());
  }

  @Test
  @DisplayName("메시지 생성 실패 - 존재하지 않는 채널")
  void createMessage_channelNotFound() {
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("Hello", channelId, authorId);

    when(channelRepository.findById(channelId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.create(request, List.of()))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  @Test
  @DisplayName("메시지 생성 실패 - 존재하지 않는 작성자")
  void createMessage_authorNotFound() {
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest("Hello", channelId, authorId);
    Channel channel = new Channel(ChannelType.PUBLIC, "test", "desc");

    when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
    when(userRepository.findById(authorId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.create(request, List.of()))
        .isInstanceOf(UserNotFoundException.class);
  }


  @Test
  @DisplayName("메시지 조회 성공")
  void findMessage_success() {
    UUID messageId = UUID.randomUUID();
    Message message = mock(Message.class);
    when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
    when(messageMapper.toDto(any())).thenReturn(mock(MessageDto.class));

    MessageDto result = messageService.find(messageId);
    assertThat(result).isNotNull();
  }

  @Test
  @DisplayName("메시지가 존재하지 않으면 조회할 수 없습니다")
  void findMessage_notFound() {
    UUID messageId = UUID.randomUUID();
    when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.find(messageId))
        .isInstanceOf(MessageNotFoundException.class);
  }

  @Test
  @DisplayName("특정 채널에 작성된 메시지 목록을 조회할 수 있습니다")
  void findAllByChannelId_success() {
    UUID channelId = UUID.randomUUID();
    Pageable pageable = PageRequest.of(0, 5);
    Slice<Message> slice = new SliceImpl<>(List.of(mock(Message.class)));

    when(messageRepository.findAllByChannelIdWithAuthor(eq(channelId), any(), eq(pageable))).thenReturn(slice);
    when(messageMapper.toDto(any())).thenReturn(mock(MessageDto.class));
    when(pageResponseMapper.fromSlice(any(), any())).thenReturn(mock(PageResponse.class));

    PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, null, pageable);

    assertThat(result).isNotNull();
  }

  @Test
  @DisplayName("메시지 업데이트 성공")
  void updateMessage_success() {
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("updated content");
    Message message = mock(Message.class);

    when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
    when(messageMapper.toDto(any())).thenReturn(mock(MessageDto.class));

    MessageDto result = messageService.update(messageId, request);
    assertThat(result).isNotNull();
    verify(message).update("updated content");
  }

  @Test
  @DisplayName("메시지를 찾을 수 없다면 업데이트를 할 수 없습니다")
  void updateMessage_notFound() {
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("update");
    when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> messageService.update(messageId, request))
        .isInstanceOf(MessageNotFoundException.class);
  }

  @Test
  @DisplayName("메시지 삭제 성공")
  void deleteMessage_success() {
    UUID messageId = UUID.randomUUID();
    when(messageRepository.existsById(messageId)).thenReturn(true);

    messageService.delete(messageId);
    verify(messageRepository).deleteById(messageId);
  }

  @Test
  @DisplayName("메시지가 존재하지 않으면 삭제할 수 없습니다")
  void deleteMessage_notFound() {
    UUID messageId = UUID.randomUUID();
    when(messageRepository.existsById(messageId)).thenReturn(false);

    assertThatThrownBy(() -> messageService.delete(messageId))
        .isInstanceOf(MessageNotFoundException.class);
  }
}
