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
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
  private final ModelMapper modelMapper;

  @Transactional
  @Override
  public UserDTO create(CreateUserRequest request,
      Optional<CreateBinaryContentRequest> binaryContentRequest) {
    userRepository.findByEmail(request.email()).ifPresent(x -> {
      throw new ServiceException(ErrorCode.DUPLICATE_EMAIL);
    });

    userRepository.findByUsername(request.username()).ifPresent(x -> {
      throw new ServiceException(ErrorCode.DUPLICATE_NAME);
    });

    BinaryContent profile = null;
    if (binaryContentRequest.isPresent()) {
      CreateBinaryContentRequest contentRequest = binaryContentRequest.get();

      profile = BinaryContent.builder()
              .bytes(contentRequest.bytes())
              .contentType(contentRequest.contentType())
              .fileName(contentRequest.fileName())
              .size(contentRequest.size())
              .build();

      binaryContentRepository.save(profile);
    }

    User createdUser = User.builder()
            .username(request.username())
            .email(request.email())
            .password(request.password())
            .profile(profile)
            .build();

    Instant lastActiveAt = Instant.now();
    UserStatus userStatus = UserStatus.builder()
            .user(createdUser)
            .lastActiveAt(lastActiveAt)
            .build();
    createdUser.setStatus(userStatus);

    userStatusRepository.save(userStatus);

    userRepository.save(createdUser);

    return modelMapper.map(createdUser, UserDTO.class);
  }

  @Transactional(readOnly = true)
  @Override
  public UserDTO find(UUID userId) {
    User findUser = userRepository.findById(userId)
        .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USER));
    UserStatus status = userStatusRepository.findByUserId(findUser.getId())
        .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USERSTATUS));
    return modelMapper.map(findUser, UserDTO.class);
  }

  @Transactional(readOnly = true)
  @Override
  public List<UserDTO> findAll() {
    return userRepository.findAll().stream()
        .map(user -> modelMapper.map(user, UserDTO.class))
        .collect(Collectors.toList());
  }

  @Transactional
  @Override
  public UserDTO update(UUID userId, UpdateUserRequest updateUserRequest,
      Optional<CreateBinaryContentRequest> binaryContentRequest) {

    User findUser = userRepository.findById(userId)
        .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USER));

    findUser.updatePassword(updateUserRequest.oldPassword(), updateUserRequest.newPassword());

    binaryContentRequest.ifPresent(contentRequest -> {
      BinaryContent newProfile = BinaryContent.builder()
              .size(contentRequest.size())
              .bytes(contentRequest.bytes())
              .contentType(contentRequest.contentType())
              .fileName(contentRequest.fileName())
              .build();

      binaryContentRepository.save(newProfile);

      if (findUser.getProfile() != null) {
        binaryContentRepository.delete(findUser.getProfile());
      }

      findUser.updateProfile(newProfile);

    });

    return modelMapper.map(findUser, UserDTO.class);
  }

  @Transactional
  @Override
  public void delete(UUID userId) {
    User deleteUser = userRepository.findById(userId)
        .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USER));

    userRepository.deleteById(userId);
  }
}
