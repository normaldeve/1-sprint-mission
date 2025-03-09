package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.user.*;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ServiceException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusRepository userStatusRepository;
  private final UserStatusService userStatusService;

  @Override
  public UserDTO create(CreateUserRequest request,
      Optional<CreateBinaryContentRequest> binaryContentRequest) {
    userRepository.findByEmail(request.email()).ifPresent(x -> {
      throw new ServiceException(ErrorCode.DUPLICATE_EMAIL);
    });

    userRepository.findByName(request.username()).ifPresent(x -> {
      throw new ServiceException(ErrorCode.DUPLICATE_NAME);
    });

    BinaryContent profile = null;
    if (binaryContentRequest.isPresent()) {
      CreateBinaryContentRequest contentRequest = binaryContentRequest.get();

      profile = new BinaryContent(contentRequest.bytes(), contentRequest.contentType(),
          contentRequest.fileName());

      binaryContentRepository.save(profile);
    }

    // 프로필이 없을 경우에도 null로 처리
    User createdUser = new User(request.username(), request.email(), request.password(),
        profile != null ? profile.getId() : null, null);
    userRepository.save(createdUser);

    Instant lastActiveAt = Instant.now();
    UserStatus userStatus = new UserStatus(createdUser.getId(), lastActiveAt);
    createdUser.setUserStatusId(userStatus.getId());

    userStatusRepository.save(userStatus);

    return UserDTO.fromDomain(createdUser);
  }

  @Override
  public UserDTO find(UUID userId) {
    User findUser = userRepository.findById(userId)
        .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USER));
    UserStatus status = userStatusRepository.findByUserId(findUser.getId())
        .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USERSTATUS));
    return UserDTO.fromDomain(findUser);
  }

  @Override
  public List<UserDTO> getOnlineUsers() {
    List<UUID> onlineUserIds = userStatusRepository.findByIsOnlineTrue().stream()
        .map(UserStatus::getUserId)
        .toList();

    return userRepository.findAllById(onlineUserIds).stream()
        .map(UserDTO::fromDomain)
        .toList();
  }

  @Override
  public List<UserDTO> findAll() {
    return userRepository.findAll().stream()
        .map(UserDTO::fromDomain)
        .collect(Collectors.toList());
  }

  @Override
  public User update(UUID userId, UpdateUserRequest updateUserRequest,
      Optional<CreateBinaryContentRequest> binaryContentRequest) {
    User findUser = userRepository.findById(userId)
        .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USER));
    findUser.updatePassword(updateUserRequest.oldPassword(), updateUserRequest.newPassword());
    if (binaryContentRequest.isPresent()) {
      if (findUser.getProfileImageId() != null) { // 이미 프로필이 등록되어 있다면 기존 프로필은 삭제
        binaryContentRepository.deleteById(findUser.getProfileImageId());
      }
      CreateBinaryContentRequest contentRequest = binaryContentRequest.get();

      BinaryContent profile = new BinaryContent(contentRequest.bytes(),
          contentRequest.contentType(),
          contentRequest.fileName());

      binaryContentRepository.save(profile);
      findUser.updateProfile(findUser.getProfileImageId(), profile.getId());
    }
    return findUser;
  }

  @Override
  public UserDTO delete(UUID id) {
    User deleteUser = userRepository.findById(id)
        .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USER));

    if (deleteUser.getProfileImageId() != null) { // 만약 프로필이 등록되어 있다면 해당 프로필도 같이 삭제
      binaryContentRepository.deleteById(deleteUser.getProfileImageId());
    }
    userStatusRepository.deleteByUserId(deleteUser.getId()); // UserStatus 도 같이 삭제

    userRepository.delete(id);

    return UserDTO.fromDomain(deleteUser);
  }
}
