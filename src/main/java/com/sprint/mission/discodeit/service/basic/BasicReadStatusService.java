package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.dto.readstatus.CreateReadStatusRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ServiceException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final ModelMapper modelMapper;

  @Transactional
  @Override
  public ReadStatusDTO create(CreateReadStatusRequest request) {
    User user = userRepository.findById(request.userId()).orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USER));
    Channel channel = channelRepository.findById(request.channelId()).orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_CHANNEL));

    List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(request.userId());
    boolean exists = readStatuses.stream()
        .anyMatch(readStatus -> readStatus.getChannel().getId().equals(request.channelId()));
    if (exists) {
      throw new ServiceException(ErrorCode.ALREADY_EXIST_READSTATUS);
    }

    Instant lastReadAt = Instant.now();
    ReadStatus readStatus = ReadStatus.builder()
            .user(user)
            .channel(channel)
            .lastReadAt(lastReadAt)
            .build();

    readStatusRepository.save(readStatus);
    return modelMapper.map(readStatus, ReadStatusDTO.class);
  }

  @Transactional(readOnly = true)
  @Override
  public ReadStatusDTO find(UUID id) {
    ReadStatus readStatus = readStatusRepository.findById(id)
            .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_READSTATUS));

    return modelMapper.map(readStatus, ReadStatusDTO.class);
  }

  @Transactional(readOnly = true)
  @Override
  public List<ReadStatusDTO> findAllByUserId(UUID userID) {
    validUser(userID);
    List<ReadStatus> allByUserId = readStatusRepository.findAllByUserId(userID);

    return allByUserId.stream()
            .map(readStatus -> modelMapper.map(readStatus, ReadStatusDTO.class))
            .collect(Collectors.toList());
  }

  /*
  User가 채널에서 메시지를 읽은 시간을 업데이트
   */
  @Transactional
  @Override
  public List<ReadStatusDTO> updateByChannelId(UUID channelId) {
    validChannel(channelId);

    // 채널에 해당하는 ReadStatus 목록을 가져옴
    List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelId(channelId);

    // 모든 ReadStatus의 'lastReadTime'을 업데이트
    readStatuses.forEach(readStatus -> readStatus.updateLastReadTime());

    return readStatuses.stream()
            .map(readStatus -> modelMapper.map(readStatus, ReadStatusDTO.class))
            .collect(Collectors.toList());
  }

  @Transactional
  @Override
  public ReadStatusDTO update(UUID id) {
    ReadStatus readStatus = readStatusRepository.findById(id)
        .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_READSTATUS));

    readStatus.updateLastReadTime();

    readStatusRepository.save(readStatus);

    return modelMapper.map(readStatus, ReadStatusDTO.class);
  }

  @Transactional
  @Override
  public void delete(UUID id) {
    Optional.ofNullable(readStatusRepository.findById(id))
            .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_READSTATUS))
            .ifPresent(readStatusRepository::delete);
  }

  private void validUser(UUID userId) {
    if (!userRepository.existsById(userId)) {
      throw new ServiceException(ErrorCode.CANNOT_FOUND_USER);
    }
  }

  private void validChannel(UUID channelId) {
    if (!channelRepository.existsById(channelId)) {
      throw new ServiceException(ErrorCode.CANNOT_FOUND_CHANNEL);
    }
  }
}
