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
    public UserDTO create(CreateUser.Request request) {
        if (!User.isValidPassword(request.getPassword())) {
            throw new ServiceException(ErrorCode.INVALID_PASSWORD);
        }

        if (!User.isValidPhone(request.getPhone())) {
            throw new ServiceException(ErrorCode.INVALID_WRITER);
        }

        userRepository.findByPhone(request.getPhone()).ifPresent(x -> {
                    throw new ServiceException(ErrorCode.DUPLICATE_PHONE);}); // 저는 이메일 대신 핸드폰 번호로 했습니다

        userRepository.findByName(request.getName()).ifPresent(x -> {
            throw new ServiceException(ErrorCode.DUPLICATE_NAME);}); // 유저의 이름이 같으면 안된다.

        if (request.getProfileId() != null) {
            binaryContentRepository.findById(request.getProfileId())
                    .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_PROFILE));
        }

        UserStatus userStatus = new UserStatus(); // UserStatus 생성
        userStatusRepository.save(userStatus);

        User createdUser = new User(request.getName(), request.getPhone(), request.getPassword(), request.getProfileId(), userStatus.getId());
        userRepository.save(createdUser); // User 레포지토리에 저장하기

        return UserDTO.fromDomain(createdUser);
    }

    @Override
    public UserDTO find(UUID userId) {
        User findUser = userRepository.findById(userId).orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USER));
        UserStatus status = userStatusRepository.findById(findUser.getUserStatusId()).orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USERSTATUS));
        return UserDTO.fromDomain(findUser);
    }

    @Override
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(UserDTO::fromDomain)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO update(UpdateUser.Request request) {
        User updateUser = userRepository.findById(request.getUserId()).orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USER));

        updateUser.update(request.getPassword()); // 비밀번호를 수정합니다.

        if (request.getContent() != null) { // 만약 요청에 content가 있다면 프로필을 새로 교체합니다.
            if (updateUser.getProfileImageId() == null) {
                // 새로운 Profile 을 생성하고 저장합니다.
                //binaryContentRepository 만들고 다시 작성할 것 !!!
            } else {
                Optional<BinaryContent> profile = binaryContentRepository.findById(updateUser.getProfileImageId());
                profile.get().updateContent(request.getContent());
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

        userStatusRepository.deleteById(deleteUser.getUserStatusId()); // UserStatus 도 같이 삭제

        userRepository.delete(id);

        return UserDTO.fromDomain(deleteUser);
    }
}
