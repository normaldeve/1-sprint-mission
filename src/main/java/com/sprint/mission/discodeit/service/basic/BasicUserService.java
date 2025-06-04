package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.type.BinaryContentUploadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.type.NotificationEvent;
import com.sprint.mission.discodeit.entity.type.NotificationType;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.JwtService;
import com.sprint.mission.discodeit.security.jwt.JwtSession;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final BinaryContentService binaryContentService;
  private final NotificationRepository notificationRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  @Override
  public UserDto create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    log.debug("사용자 생성 시작: {}", userCreateRequest);

    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    if (userRepository.existsByEmail(email)) {
      throw UserAlreadyExistsException.withEmail(email);
    }
    if (userRepository.existsByUsername(username)) {
      throw UserAlreadyExistsException.withUsername(username);
    }

    String password = userCreateRequest.password();

    String hashedPassword = passwordEncoder.encode(password);
    User user = new User(username, email, hashedPassword, null);

    userRepository.save(user);

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(profileRequest -> {
          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          binaryContentRepository.save(binaryContent);
          extractedTransaction(binaryContent, bytes, user);
          return binaryContent;
        })
        .orElse(null);

    user.setProfile(nullableProfile);

    log.info("사용자 생성 완료: id={}, username={}", user.getId(), username);
    return userMapper.toDto(user);
  }

  @Transactional(readOnly = true)
  @Override
  public UserDto find(UUID userId) {
    log.debug("사용자 조회 시작: id={}", userId);
    UserDto userDto = userRepository.findById(userId)
        .map(userMapper::toDto)
        .orElseThrow(() -> UserNotFoundException.withId(userId));
    log.info("사용자 조회 완료: id={}", userId);
    return userDto;
  }

  @Transactional(readOnly = true)
  public UserDto findByName(String username) {
    log.debug("사용자 조회 시작 : name = {}", username);
    UserDto userDto = userRepository.findByUsername(username)
        .map(userMapper::toDto)
        .orElseThrow(() -> UserNotFoundException.withUsername(username));

    log.info("사용자 조회 완료: {}", username);

    return userDto;

  }

  @Override
  public List<UserDto> findAll() {
    log.debug("모든 사용자 조회 시작");
    Set<UUID> onlineUserIds = jwtService.getActiveJwtSessions().stream()
        .map(JwtSession::getUserId)
        .collect(Collectors.toSet());

    List<UserDto> userDtos = userRepository.findAllWithProfile()
        .stream()
        .map(user -> userMapper.toDto(user, onlineUserIds.contains(user.getId())))
        .toList();
    log.info("모든 사용자 조회 완료: 총 {}명", userDtos.size());
    return userDtos;
  }

  @PreAuthorize("hasRole('ADMIN') or principal.userDto.id == #userId")
  @Transactional
  @Override
  public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    log.debug("사용자 수정 시작: id={}, request={}", userId, userUpdateRequest);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          UserNotFoundException exception = UserNotFoundException.withId(userId);
          return exception;
        });

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();

    if (userRepository.existsByEmail(newEmail)) {
      throw UserAlreadyExistsException.withEmail(newEmail);
    }

    if (userRepository.existsByUsername(newUsername)) {
      throw UserAlreadyExistsException.withUsername(newUsername);
    }

    String newPassword = userUpdateRequest.newPassword();
    String hashedNewPassword = Optional.ofNullable(newPassword).map(passwordEncoder::encode)
        .orElse(null);
    user.update(newUsername, newEmail, hashedNewPassword, null);

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(profileRequest -> {

          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          binaryContentRepository.save(binaryContent);
          extractedTransaction(binaryContent, bytes, user);
          return binaryContent;
        })
        .orElse(null);

    user.setProfile(nullableProfile);

    log.info("사용자 수정 완료: id={}", userId);
    return userMapper.toDto(user);
  }

  private void extractedTransaction(BinaryContent binaryContent, byte[] bytes, User user) {
    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override
      public void afterCommit() {
        binaryContentStorage.put(binaryContent.getId(), bytes)
            .thenRun(() -> {
              binaryContentService.updateStatus(binaryContent.getId(), BinaryContentUploadStatus.SUCCESS);
            })
            .exceptionally(ex -> {
              binaryContentService.updateStatus(binaryContent.getId(), BinaryContentUploadStatus.FAILED);
              eventPublisher.publishEvent(new NotificationEvent(user, "프로필 이미지 업로드 실패",
                  "파일 업로드 중 오류가 발생했습니다.",
                  NotificationType.ASYNC_FAILED, null));

              return null;
            });
      }
    });
  }

  @PreAuthorize("hasRole('ADMIN') or principal.userDto.id == #userId")
  @Transactional
  @Override
  public void delete(UUID userId) {
    log.debug("사용자 삭제 시작: id={}", userId);

    if (!userRepository.existsById(userId)) {
      throw UserNotFoundException.withId(userId);
    }

    userRepository.deleteById(userId);
    log.info("사용자 삭제 완료: id={}", userId);
  }
}
