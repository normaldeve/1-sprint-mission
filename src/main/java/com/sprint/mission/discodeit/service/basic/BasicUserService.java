package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.domain.BinaryContent;
import com.sprint.mission.discodeit.domain.User;
import com.sprint.mission.discodeit.domain.UserStatus;
import com.sprint.mission.discodeit.dto.user.FindUserDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.error.ErrorCode;
import com.sprint.mission.discodeit.exception.ServiceException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserDto create(UserCreateRequest userCreateRequest) {
        if (!User.isValidPassword(userCreateRequest.password())) {
            throw new ServiceException(ErrorCode.INVALID_PASSWORD);
        }

        if (!User.isValidPhone(userCreateRequest.phone())) {
            throw new ServiceException(ErrorCode.INVALID_WRITER);
        }

        userRepository.findByPhone(userCreateRequest.phone()).ifPresent(x -> {
                    throw new ServiceException(ErrorCode.DUPLICATE_PHONE);}); // 저는 이메일 대신 핸드폰 번호로 했습니다

        userRepository.findByName(userCreateRequest.name()).ifPresent(x -> {
            throw new ServiceException(ErrorCode.DUPLICATE_NAME);}); // 유저의 이름이 같으면 안된다.

        BinaryContent profile = userCreateRequest.profileImageId() == null
                ? binaryContentRepository.findById(userCreateRequest.profileImageId()) .orElseThrow()// UserCreateRequest에서 profileImageId가 null이 아니면 가져오기
                : null;

        User createdUser = new User(
                userCreateRequest.name(),
                userCreateRequest.phone(),
                userCreateRequest.password(),
                Optional.ofNullable(profile).map(BinaryContent::getId)
        );

        userRepository.save(createdUser);

        UserStatus userStatus = new UserStatus(createdUser.getId()); // UserStatus 생성
        userStatusRepository.save(userStatus);

        return new UserDto(createdUser.getId(), createdUser.getName(), createdUser.getPhone());
    }

    @Override
    public FindUserDto getUserByPhone(String phone) {
        Optional<User> findUser = userRepository.findByPhone(phone);
        UserStatus userStatus = userStatusRepository.findByUserId(findUser.get().getId());
        return new FindUserDto(findUser.get().getId(), findUser.get().getName(), findUser.get().getPhone(), userStatus);
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User updateUserPassword(User user, String newPass) {
        existUser(user);
        user.update(newPass);
        userRepository.save(user);
        return user;
    }

    @Override
    public void delete(User removeUser) {
        existUser(removeUser);
        userRepository.delete(removeUser);
    }

    private void existUser(User user) {
        userRepository.findByPhone(user.getPhone())
                .orElseThrow(() -> new ServiceException(ErrorCode.CANNOT_FOUND_USER));
    }
}
