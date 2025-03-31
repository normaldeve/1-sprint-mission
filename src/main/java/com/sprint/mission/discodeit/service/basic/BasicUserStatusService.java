package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.ip.RequestIPContext;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusMapper userStatusMapper;
  private final RequestIPContext requestIPContext;

  @Transactional
  @Override
  public UserStatusDto create(UserStatusCreateRequest request) {
    UUID userId = request.userId();
    log.info("[유저 상태 생성 요청] userId: {}", userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("[유저 상태 생성 실패] 존재하지 않는 사용자: {}", userId);
          return new UserNotFoundException(ErrorCode.CANNOT_FOUND_USER, Map.of("userId", userId, "requestIp", requestIPContext.getClientIp()));
        });

    Optional.ofNullable(user.getStatus()).ifPresent(status -> {
      log.warn("[유저 상태 생성 실패] 이미 상태가 존재하는 사용자: {}", userId);
      throw new UserStatusException(ErrorCode.ALREADY_EXIST_USERSTAUTS, Map.of("userId", userId, "requestIp", requestIPContext.getClientIp()));
    });

    Instant lastActiveAt = request.lastActiveAt();
    UserStatus userStatus = new UserStatus(user, lastActiveAt);
    userStatusRepository.save(userStatus);

    log.info("[유저 상태 생성 완료] userId: {}, lastActiveAt: {}", userId, lastActiveAt);
    return userStatusMapper.toDto(userStatus);
  }

  @Override
  public UserStatusDto find(UUID userStatusId) {
    log.debug("[유저 상태 조회] userStatusId: {}", userStatusId);
    return userStatusRepository.findById(userStatusId)
        .map(userStatusMapper::toDto)
        .orElseThrow(() -> {
          log.warn("[유저 상태 조회 실패] 존재하지 않는 상태 ID: {}", userStatusId);
          return new UserStatusNotFoundException(ErrorCode.CANNOT_FOUND_USERSTATUS, Map.of("userStatusId", userStatusId, "requestIp", requestIPContext.getClientIp()));
        });
  }

  @Override
  public List<UserStatusDto> findAll() {
    log.debug("[전체 유저 상태 목록 조회]");
    return userStatusRepository.findAll().stream()
        .map(userStatusMapper::toDto)
        .toList();
  }

  @Transactional
  @Override
  public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request) {
    log.info("[유저 상태 수정 요청] userStatusId: {}", userStatusId);

    UserStatus userStatus = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> {
          log.warn("[유저 상태 수정 실패] 존재하지 않는 상태 ID: {}", userStatusId);
          return new UserStatusNotFoundException(ErrorCode.CANNOT_FOUND_USERSTATUS, Map.of("userStatusId", userStatusId, "requestIp", requestIPContext.getClientIp()));
        });

    Instant newLastActiveAt = request.newLastActiveAt();
    userStatus.update(newLastActiveAt);

    log.info("[유저 상태 수정 완료] userStatusId: {}, newLastActiveAt: {}", userStatusId, newLastActiveAt);
    return userStatusMapper.toDto(userStatus);
  }

  @Transactional
  @Override
  public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    log.info("[유저 상태 수정 요청 - userId 기준] userId: {}", userId);

    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> {
          log.warn("[유저 상태 수정 실패] 상태가 존재하지 않는 사용자: {}", userId);
          return new UserStatusNotFoundException(ErrorCode.CANNOT_FOUND_USERSTATUS, Map.of("userId", userId, "userStatusId", userId, "requestIp", requestIPContext.getClientIp()));
        });

    Instant newLastActiveAt = request.newLastActiveAt();
    userStatus.update(newLastActiveAt);

    log.info("[유저 상태 수정 완료 - userId 기준] userId: {}, newLastActiveAt: {}", userId, newLastActiveAt);
    return userStatusMapper.toDto(userStatus);
  }

  @Transactional
  @Override
  public void delete(UUID userStatusId) {
    log.info("[유저 상태 삭제 요청] userStatusId: {}", userStatusId);

    if (!userStatusRepository.existsById(userStatusId)) {
      log.warn("[유저 상태 삭제 실패] 존재하지 않는 상태 ID: {}", userStatusId);
      throw new UserStatusNotFoundException(ErrorCode.CANNOT_FOUND_USERSTATUS, Map.of("userStatusId", userStatusId, "requestIp", requestIPContext.getClientIp()));
    }

    userStatusRepository.deleteById(userStatusId);
    log.info("[유저 상태 삭제 완료] userStatusId: {}", userStatusId);
  }
}

