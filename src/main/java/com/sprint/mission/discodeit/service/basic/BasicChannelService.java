package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelMapper channelMapper;

  @Transactional
  @Override
  public ChannelDto create(PublicChannelCreateRequest request) {
    String name = request.name();
    String description = request.description();
    Channel channel = new Channel(ChannelType.PUBLIC, name, description);
    log.info("[공개 채널 생성 시도] type:  {}, name: {}", channel.getType(), channel.getName());

    channelRepository.save(channel);

    log.info("[공개 채널 생성 성공] id: {}", channel.getId());
    return channelMapper.toDto(channel);
  }

  @Transactional
  @Override
  public ChannelDto create(PrivateChannelCreateRequest request) {
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);

    channelRepository.save(channel);

    log.info("[개인 채널 생성 완료] id: {}", channel.getId());

    List<ReadStatus> readStatuses = userRepository.findAllById(request.participantIds()).stream()
        .map(user -> new ReadStatus(user, channel, channel.getCreatedAt()))
        .toList();
    readStatusRepository.saveAll(readStatuses);

    log.info("[읽기 정보 생성 완료] count: {}", readStatuses.size());

    return channelMapper.toDto(channel);
  }

  @Transactional(readOnly = true)
  @Override
  public ChannelDto find(UUID channelId) {
    return channelRepository.findById(channelId)
        .map(channelMapper::toDto)
        .orElseThrow(
            () -> {
              log.warn("[채널 조회 실패] 해당 채널을 찾을 수 없습니다 id: {}", channelId);
              return new ChannelNotFoundException(ErrorCode.CANNOT_FOUND_CHANNEL, Map.of("channelId", channelId));});
  }

  @Transactional(readOnly = true)
  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
        .map(ReadStatus::getChannel)
        .map(Channel::getId)
        .toList();

    log.info("[채널 조회 성공] id: {}", userId);
    return channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, mySubscribedChannelIds)
        .stream()
        .map(channelMapper::toDto)
        .toList();
  }

  @Transactional
  @Override
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
    String newName = request.newName();
    String newDescription = request.newDescription();
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> {
              log.warn("[채널 업데이트 실패] 해당 채널을 찾을 수 없습니다 id: {}", channelId);
              return new ChannelNotFoundException(ErrorCode.CANNOT_FOUND_CHANNEL,
                  Map.of("channelId", channelId));});
    if (channel.getType().equals(ChannelType.PRIVATE)) {
      log.warn("[채널 업데이트 실패] 개인 채널은 업데이트 할 수 없습니다 type: {}", channel.getType());
      throw new ChannelException(ErrorCode.CANNOT_MODIFY_PRIVATE_CHANNEL, Map.of("channelId", channelId));
    }
    channel.update(newName, newDescription);
    log.info("[채널 업데이트 성공] id: {}", channel.getId());
    return channelMapper.toDto(channel);
  }

  @Transactional
  @Override
  public void delete(UUID channelId) {
    if (!channelRepository.existsById(channelId)) {
      log.warn("[채널 삭제 실패] 해당 채널을 찾을 수 없습니다 id: {}", channelId);
      throw new ChannelNotFoundException(ErrorCode.CANNOT_FOUND_CHANNEL, Map.of("channelId", channelId));
    }

    messageRepository.deleteAllByChannelId(channelId);
    readStatusRepository.deleteAllByChannelId(channelId);

    channelRepository.deleteById(channelId);

    log.info("[채널 삭제 완료] id: {}", channelId);
  }
}
