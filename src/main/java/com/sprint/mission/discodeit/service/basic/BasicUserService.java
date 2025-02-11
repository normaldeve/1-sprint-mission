package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.domain.BinaryContent;
import com.sprint.mission.discodeit.domain.User;
import com.sprint.mission.discodeit.domain.UserStatus;
import com.sprint.mission.discodeit.dto.user.*;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ServiceException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserDTO create(CreateUserRequest request) {
        if (!User.isValidPassword(request.password())) {
            throw new ServiceException(ErrorCode.INVALID_PASSWORD);
        }

        if (!User.isValidPhone(request.phone())) {
            throw new ServiceException(ErrorCode.INVALID_WRITER);
        }

        userRepository.findByPhone(request.phone()).ifPresent(x -> {
                    throw new ServiceException(ErrorCode.DUPLICATE_PHONE);}); // 저는 이메일 대신 핸드폰 번호로 했습니다

        userRepository.findByName(request.name()).ifPresent(x -> {
            throw new ServiceException(ErrorCode.DUPLICATE_NAME);}); // 유저의 이름이 같으면 안된다.

        if (request.profileId() != null) {
            binaryContentRepository.findById(request.profileId())
                    .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_PROFILE));
        }

        User createdUser = new User(request.name(), request.phone(), request.password(), request.profileId());
        userRepository.save(createdUser); // User 레포지토리에 저장하기

        Instant lastActiveAt = Instant.now();
        UserStatus userStatus = new UserStatus(createdUser.getId(), lastActiveAt); // UserStatus 생성
        userStatusRepository.save(userStatus);

        return UserDTO.fromDomain(createdUser);
    }

    @Override
    public UserDTO find(UUID userId) {
        User findUser = userRepository.findById(userId).orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USER));
        UserStatus status = userStatusRepository.findByUserId(findUser.getId()).orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USERSTATUS));
        return UserDTO.fromDomain(findUser);
    }

    @Override
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(UserDTO::fromDomain)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO update(UpdateUser request) {
        User updateUser = userRepository.findById(request.userId()).orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USER));

        updateUser.update(request.password()); // 비밀번호를 수정합니다.

        if (request.profile() != null) { // 만약 요청에 content가 있다면 프로필을 새로 교체합니다.
            if (updateUser.getProfileImageId() == null) {
                // 새로운 Profile 을 생성하고 저장합니다.
                //binaryContentRepository 만들고 다시 작성할 것 !!!
            } else {
                Optional<BinaryContent> profile = binaryContentRepository.findById(updateUser.getProfileImageId());
                profile.get().updateContent(request.profile());
            }
        }

        userRepository.save(updateUser);
        return UserDTO.fromDomain(updateUser);
    }

    @Override
    public UserDTO delete(UUID id) {
        User deleteUser = userRepository.findById(id).orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USER));

        if (deleteUser.getProfileImageId() != null) { // 만약 프로필이 등록되어 있다면 해당 프로필도 같이 삭제
            binaryContentRepository.deleteById(deleteUser.getProfileImageId());
        }

        userStatusRepository.deleteByUserId(deleteUser.getId()); // UserStatus 도 같이 삭제

        userRepository.delete(id);

        return UserDTO.fromDomain(deleteUser);
    }
}
