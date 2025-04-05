package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.time.Instant;
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
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusMapper readStatusMapper;

  @Transactional
  @Override
  public ReadStatusDto create(ReadStatusCreateRequest request) {
    UUID userId = request.userId();
    UUID channelId = request.channelId();

    User user = userRepository.findById(userId)
        .orElseThrow(
                () -> {
                  log.warn("[읽기 정보 생성 실패] 해당하는 회원을 찾을 수 없습니다");
                  return new UserNotFoundException(ErrorCode.CANNOT_FOUND_USER, Map.of("userId", userId));});
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> {
              log.warn("[읽기 정보 생성 실패] 해당하는 채널을 찾을 수 없습니다");
              return new ChannelNotFoundException(ErrorCode.CANNOT_FOUND_CHANNEL, Map.of("channelId", channelId));});

    if (readStatusRepository.existsByUserIdAndChannelId(user.getId(), channel.getId())) {
      log.warn("[읽기 정보 생성 실패] 회원과 채널에 해당하는 읽기 정보가 이미 존재합니다");
      throw new ReadStatusNotFoundException(ErrorCode.ALREADY_EXIST_READSTATUS, Map.of("userId", userId, "channelId", channelId));
    }

    Instant lastReadAt = request.lastReadAt();
    ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);
    readStatusRepository.save(readStatus);

    log.info("[읽기 정보 생성 완료] 읽기 정보가 생성되었습니다 id: {}", readStatus.getId());

    return readStatusMapper.toDto(readStatus);
  }

  @Override
  public ReadStatusDto find(UUID readStatusId) {
    return readStatusRepository.findById(readStatusId)
        .map(readStatusMapper::toDto)
        .orElseThrow(
            () ->{
              log.warn("[읽기 정보 조회 실패] 해당하는 정보를 찾을 수 없습니다");
              return new ReadStatusNotFoundException(ErrorCode.CANNOT_FOUND_READSTATUS, Map.of("readStatusId", readStatusId));}
            );
  }

  @Override
  public List<ReadStatusDto> findAllByUserId(UUID userId) {
    return readStatusRepository.findAllByUserId(userId).stream()
        .map(readStatusMapper::toDto)
        .toList();
  }

  @Transactional
  @Override
  public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
    Instant newLastReadAt = request.newLastReadAt();
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(
            () ->{
              log.warn("[읽기 정보 업데이트 실패] 해당하는 정보를 찾을 수 없습니다");
              return new ReadStatusNotFoundException(ErrorCode.CANNOT_FOUND_READSTATUS, Map.of("readStatusId", readStatusId));});
    readStatus.update(newLastReadAt);

    log.info("[읽기 정보 업데이트 성공] 해당 정보를 업데이트 하였습니다 id: {}", readStatus.getId());
    return readStatusMapper.toDto(readStatus);
  }

  @Transactional
  @Override
  public void delete(UUID readStatusId) {
    if (!readStatusRepository.existsById(readStatusId)) {
      log.warn("[읽기 정보 삭제 실패] 해당 정보를 찾을 수 없습니다");
      throw new ReadStatusNotFoundException(ErrorCode.CANNOT_FOUND_READSTATUS, Map.of("readStatusId", readStatusId));
    }

    readStatusRepository.deleteById(readStatusId);
    log.info("[읽기 정보 삭제 완료]");
  }
}
