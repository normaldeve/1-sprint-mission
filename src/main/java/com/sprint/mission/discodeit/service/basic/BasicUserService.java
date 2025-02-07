package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.domain.BinaryContent;
import com.sprint.mission.discodeit.domain.User;
import com.sprint.mission.discodeit.domain.UserStatus;
import com.sprint.mission.discodeit.dto.user.CreateUser;
import com.sprint.mission.discodeit.dto.user.UpdateUser;
import com.sprint.mission.discodeit.dto.user.UserDTO;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ServiceException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.type.ProfileUse;
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
    public CreateUser.Response create(CreateUser.Request request) {
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

        return new CreateUser.Response(createdUser.getId(), createdUser.getProfileImageId() == null ? ProfileUse.NO : ProfileUse.YES);
    }

    @Override
    public UserDTO find(UUID userId) {
        User findUser = userRepository.findById(userId).orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USER));
        UserStatus status = userStatusRepository.findById(findUser.getUserStatusId()).orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USERSTATUS));
        return new UserDTO(findUser.getId(), findUser.getName(), findUser.getPhone(), status);
    }

    @Override
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UpdateUser.Response update(UpdateUser.Request request) {
        User updateUser = userRepository.findById(request.getUserId()).orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USER));

        BinaryContent profile = binaryContentRepository.findById(request.getProfileId()).orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_PROFILE));
        updateUser.setProfileImageId(profile.getId());

        return new UpdateUser.Response(updateUser.getId(), ProfileUse.YES);
    }

    @Override
    public void delete(User removeUser) {
        existUser(removeUser);
        userRepository.delete(removeUser);
        userStatusRepository.deleteById(removeUser.getUserStatusId());
        binaryContentRepository.deleteById(removeUser.getProfileImageId());
    }

    private UserDTO convertToUserDTO(User user) {
        UserStatus status = userStatusRepository.findById(user.getUserStatusId())
                .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USERSTATUS));

        return new UserDTO(user.getId(), user.getName(), user.getPhone(), status);
    }
}
