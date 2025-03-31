package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.ip.RequestIPContext;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
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
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final RequestIPContext requestIPContext;

  @Transactional
  @Override
  public UserDto create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {

    String username = userCreateRequest.username();
    String email = userCreateRequest.email();
    log.info("[회원 생성 요청] username: {}, email: {}", username, email);

    if (userRepository.existsByEmail(email)) {
      log.warn("[회원 생성 실패] 중복된 이메일: {}", email);
      throw new UserAlreadyExistException(ErrorCode.DUPLICATE_EMAIL, Map.of("email", email , "requestIp", requestIPContext.getClientIp()));
    }
    if (userRepository.existsByUsername(username)) {
      log.warn("[회원 생성 실패] 중복된 사용자 이름: {}", username);
      throw new UserAlreadyExistException(ErrorCode.DUPLICATE_NAME, Map.of("username", username , "requestIp", requestIPContext.getClientIp()));
    }

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(profileRequest -> {
          log.info("[프로필 이미지 업로드] 파일명: {}, 타입: {}, 크기: {} bytes",
              profileRequest.fileName(), profileRequest.contentType(), profileRequest.bytes().length);

          BinaryContent binaryContent = new BinaryContent(
              profileRequest.fileName(),
              (long) profileRequest.bytes().length,
              profileRequest.contentType()
          );
          binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(binaryContent.getId(), profileRequest.bytes());
          return binaryContent;
        })
        .orElse(null);

    User user = new User(username, email, userCreateRequest.password(), nullableProfile);
    Instant now = Instant.now();
    UserStatus userStatus = new UserStatus(user, now);

    userRepository.save(user);
    log.info("[회원 생성 완료] username: {}, id: {}", username, user.getId());

    return userMapper.toDto(user);
  }

  @Override
  public UserDto find(UUID userId) {
    log.debug("[회원 조회] id: {}", userId);
    return userRepository.findById(userId)
        .map(userMapper::toDto)
        .orElseThrow(() -> {
          log.warn("[회원 조회 실패] 존재하지 않는 사용자: {}", userId);
          return new UserNotFoundException(ErrorCode.CANNOT_FOUND_USER, Map.of("userId", userId , "requestIp", requestIPContext.getClientIp()));
        });
  }

  @Override
  public List<UserDto> findAll() {
    log.debug("[전체 회원 목록 조회]");
    return userRepository.findAllWithProfileAndStatus().stream()
        .map(userMapper::toDto)
        .toList();
  }

  @Transactional
  @Override
  public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {

    log.info("[회원 수정 요청] id: {}", userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("[회원 수정 실패] 존재하지 않는 사용자: {}", userId);
          return new UserNotFoundException(ErrorCode.CANNOT_FOUND_USER, Map.of("userId", userId, "requestIp", requestIPContext.getClientIp()));
        });

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();

    if (userRepository.existsByEmail(newEmail)) {
      log.warn("[회원 수정 실패] 중복된 이메일: {}", newEmail);
      throw new UserAlreadyExistException(ErrorCode.DUPLICATE_EMAIL, Map.of("email", newEmail , "requestIp", requestIPContext.getClientIp()));
    }
    if (userRepository.existsByUsername(newUsername)) {
      log.warn("[회원 수정 실패] 중복된 사용자 이름: {}", newUsername);
      throw new UserAlreadyExistException(ErrorCode.DUPLICATE_NAME, Map.of("username", newUsername , "requestIp", requestIPContext.getClientIp()));
    }

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(profileRequest -> {
          log.info("[프로필 이미지 업데이트] 파일명: {}, 타입: {}, 크기: {} bytes",
              profileRequest.fileName(), profileRequest.contentType(), profileRequest.bytes().length);

          BinaryContent binaryContent = new BinaryContent(
              profileRequest.fileName(),
              (long) profileRequest.bytes().length,
              profileRequest.contentType()
          );
          binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(binaryContent.getId(), profileRequest.bytes());
          return binaryContent;
        })
        .orElse(null);

    user.update(newUsername, newEmail, userUpdateRequest.newPassword(), nullableProfile);
    log.info("[회원 수정 완료] id: {}, 새로운 이름: {}, 새로운 이메일: {}", userId, newUsername, newEmail);

    return userMapper.toDto(user);
  }

  @Transactional
  @Override
  public void delete(UUID userId) {
    log.info("[회원 삭제 요청] id: {}", userId);

    if (!userRepository.existsById(userId)) {
      log.warn("[회원 삭제 실패] 존재하지 않는 사용자: {}", userId);
      throw new UserNotFoundException(ErrorCode.CANNOT_FOUND_USER, Map.of("userId", userId , "requestIp", requestIPContext.getClientIp()));
    }

    userRepository.deleteById(userId);
    log.info("[회원 삭제 완료] id: {}", userId);
  }
}

