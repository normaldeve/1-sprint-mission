package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ServiceException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.BDDMockito.*;

@ActiveProfiles("dev")
@ExtendWith(MockitoExtension.class)
public class BasicChannelServiceTest {

  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private ReadStatusRepository readStatusRepository;
  @Mock
  private MessageRepository messageRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ChannelMapper channelMapper;
  @InjectMocks
  private BasicChannelService channelService;

  @Test
  @DisplayName("성공 - 공개 채널 생성")
  void createPublicChannel_success() {
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("공지방", "안내용");
    Channel channel = new Channel(ChannelType.PUBLIC, "공지방", "안내용");
    ChannelDto dto = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "공지방", "안내용", null,
        null);

    given(channelRepository.save(any())).willReturn(channel);
    given(channelMapper.toDto(any())).willReturn(dto);

    ChannelDto result = channelService.create(request);

    assertThat(result.name()).isEqualTo("공지방");
    then(channelRepository).should().save(any());
  }

  @Test
  @DisplayName("성공 - 개인 채널 생성")
  void createPrivateChannel_success() {
    List<UUID> users = List.of(UUID.randomUUID(), UUID.randomUUID());
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(users);
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    ChannelDto dto = new ChannelDto(UUID.randomUUID(), ChannelType.PRIVATE, null, null, null, null);

    given(channelRepository.save(any())).willReturn(channel);
    given(userRepository.findAllById(users)).willReturn(
        users.stream().map(id -> new User("user", "email", "pw", null)).toList()
    );
    given(readStatusRepository.saveAll(any())).willReturn(List.of());
    given(channelMapper.toDto(any())).willReturn(dto);

    ChannelDto result = channelService.create(request);

    assertThat(result.type()).isEqualTo(ChannelType.PRIVATE);
    then(readStatusRepository).should().saveAll(any());
  }

  @Test
  @DisplayName("실패 - 채널 조회")
  void findChannel_fail_notFound() {
    UUID channelId = UUID.randomUUID();
    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> channelService.find(channelId))
        .isInstanceOf(ServiceException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CANNOT_FOUND_CHANNEL);
  }

  @Test
  @DisplayName("실패 - 개인 채널은 수정 불가")
  void updateChannel_fail_privateChannel() {
    UUID channelId = UUID.randomUUID();
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("newChannel", "hello");

    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

    assertThatThrownBy(() -> channelService.update(channelId, request))
        .isInstanceOf(ServiceException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CANNOT_MODIFY_PRIVATE_CHANNEL);
  }

  @Test
  @DisplayName("성공 - 채널 삭제")
  void deleteChannel_success() {
    UUID channelId = UUID.randomUUID();
    given(channelRepository.existsById(channelId)).willReturn(true);

    channelService.delete(channelId);

    then(messageRepository).should().deleteAllByChannelId(channelId);
    then(readStatusRepository).should().deleteAllByChannelId(channelId);
    then(channelRepository).should().deleteById(channelId);
  }

  @Test
  @DisplayName("실패 - 채널 삭제")
  void deleteChannel_fail_notFound() {
    UUID channelId = UUID.randomUUID();
    given(channelRepository.existsById(channelId)).willReturn(false);

    assertThatThrownBy(() -> channelService.delete(channelId))
        .isInstanceOf(ServiceException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CANNOT_FOUND_CHANNEL);
  }
}
