package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;

@Import(BasicChannelService.class)
@ExtendWith(MockitoExtension.class)
public class BasicChannelServiceTest {
  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private ChannelMapper channelMapper;
  @Mock
  private ReadStatusRepository readStatusRepository;
  @Mock
  private MessageRepository messageRepository;
  @Mock
  private UserRepository userRepository;
  @InjectMocks
  private BasicChannelService channelService;

  @Test
  @DisplayName("공개 채널 생성 시 이름과 설명으로 생성된다")
  void createPublicChannel() {
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("test", "desc");
    Channel channel = new Channel(ChannelType.PUBLIC, "test", "desc");

    when(channelRepository.save(any())).thenReturn(channel);
    when(channelMapper.toDto(any())).thenReturn(
        new ChannelDto(channel.getId(), channel.getType(), channel.getName(), channel.getDescription(), null, null));

    ChannelDto result = channelService.create(request);

    assertThat(result.name()).isEqualTo("test");
    verify(channelRepository).save(any());
  }

  @Test
  @DisplayName("개인 채널 생성 시 참여자에 대한 ReadStatus도 생성된다")
  void createPrivateChannel() {
    List<UUID> userIds = List.of(UUID.randomUUID(), UUID.randomUUID());
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(userIds);
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);

    List<User> users = userIds.stream().map(id -> {
      User u = new User("u", "e@e.com", "pass", null);
      ReflectionTestUtils.setField(u, "id", id);
      return u;
    }).toList();

    List<UserDto> participants = userIds.stream().map(id ->
        new UserDto(id, "u", "e@e.com", null, null)
    ).toList();

    when(channelRepository.save(any())).thenReturn(channel);
    when(userRepository.findAllById(userIds)).thenReturn(users);
    when(readStatusRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));
    when(channelMapper.toDto(any())).thenReturn(
        new ChannelDto(channel.getId(), channel.getType(), null, null, participants, null));

    ChannelDto result = channelService.create(request);

    assertThat(result).isNotNull();
    verify(readStatusRepository).saveAll(any());
  }

  @Test
  @DisplayName("존재하지 않는 채널 ID로 조회하면 예외가 발생한다")
  void findChannel_notFound() {
    UUID id = UUID.randomUUID();
    when(channelRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> channelService.find(id))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  @Test
  @DisplayName("사용자의 참여 채널과 공개 채널을 모두 조회할 수 있다")
  void findAllByUserId_success_withMultipleChannels() {
    UUID userId = UUID.randomUUID();

    Channel publicChannel1 = new Channel(ChannelType.PUBLIC, "Public1", "Desc1");
    Channel publicChannel2 = new Channel(ChannelType.PUBLIC, "Public2", "Desc2");
    Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);

    ReadStatus status = new ReadStatus(new User("test", "test@naver.com", "Testpass1234!", null), privateChannel, Instant.now());

    List<Channel> allChannels = List.of(publicChannel1, publicChannel2, privateChannel);

    when(readStatusRepository.findAllByUserId(userId)).thenReturn(List.of(status));
    when(channelRepository.findAllByTypeOrIdIn(eq(ChannelType.PUBLIC), any())).thenReturn(allChannels);

    when(channelMapper.toDto(publicChannel1))
        .thenReturn(new ChannelDto(publicChannel1.getId(), ChannelType.PUBLIC,"Public1", "Desc1", null, null));
    when(channelMapper.toDto(publicChannel2))
        .thenReturn(new ChannelDto(publicChannel2.getId(), ChannelType.PUBLIC,"Public2", "Desc2", null, null));
    when(channelMapper.toDto(privateChannel))
        .thenReturn(new ChannelDto(privateChannel.getId(), ChannelType.PRIVATE,null, null, null , null));

    List<ChannelDto> result = channelService.findAllByUserId(userId);

    assertThat(result).hasSize(3);
    assertThat(result)
        .extracting(ChannelDto::type)
        .containsExactlyInAnyOrder(ChannelType.PUBLIC, ChannelType.PUBLIC, ChannelType.PRIVATE);
  }


  @Test
  @DisplayName("공개 채널 수정 성공")
  void updatePublicChannel() {
    UUID id = UUID.randomUUID();
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("newName", "newDesc");
    Channel channel = new Channel(ChannelType.PUBLIC, "old", "desc");

    when(channelRepository.findById(id)).thenReturn(Optional.of(channel));
    when(channelMapper.toDto(any())).thenReturn(
        new ChannelDto(id, ChannelType.PUBLIC, "newName", "newDesc", null, null));

    ChannelDto result = channelService.update(id, request);

    assertThat(result.name()).isEqualTo("newName");
    verify(channelRepository).findById(id);
  }

  @Test
  @DisplayName("존재하지 않는 채널은 업데이트 할 수 없다")
  void updateChannel_notFound() {
    UUID channelId = UUID.randomUUID();
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("newName", "newDescription");

    when(channelRepository.findById(channelId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> channelService.update(channelId, request))
        .isInstanceOf(ChannelNotFoundException.class);

    verifyNoMoreInteractions(channelMapper);
  }


  @Test
  @DisplayName("개인 채널은 수정할 수 없고 예외를 발생시킨다")
  void updatePrivateChannel() {
    UUID id = UUID.randomUUID();
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("name", "desc");
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);

    when(channelRepository.findById(id)).thenReturn(Optional.of(channel));

    assertThatThrownBy(() -> channelService.update(id, request))
        .isInstanceOf(ChannelException.class);
  }

  @Test
  @DisplayName("존재하지 않는 채널 삭제 시 예외가 발생한다")
  void deleteChannel_notFound() {
    UUID id = UUID.randomUUID();
    when(channelRepository.existsById(id)).thenReturn(false);

    assertThatThrownBy(() -> channelService.delete(id))
        .isInstanceOf(ChannelNotFoundException.class);
  }

  @Test
  @DisplayName("채널 삭제 시 메시지와 읽기 상태도 함께 삭제된다")
  void deleteChannel_cascadesRelatedEntities() {
    UUID id = UUID.randomUUID();
    when(channelRepository.existsById(id)).thenReturn(true);

    channelService.delete(id);

    verify(messageRepository).deleteAllByChannelId(id);
    verify(readStatusRepository).deleteAllByChannelId(id);
    verify(channelRepository).deleteById(id);
  }
}
