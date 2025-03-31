package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dev")
@ExtendWith(MockitoExtension.class)
public class BasicUserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @Mock
  private BinaryContentRepository binaryContentRepository;

  @Mock
  private BinaryContentStorage binaryContentStorage;

  @InjectMocks
  private BasicUserService userService;

  @Test
  @DisplayName("성공 - 유저 생성")
  void createUser_success() {
    UserCreateRequest request = new UserCreateRequest("rex", "rex@naver.com", "Rlawnsdn12!");
    User user = new User(request.username(), request.email(), request.password(), null);
    UserDto userDto = new UserDto(UUID.randomUUID(), "rex", "rex@naver.com", null, true);

    given(userRepository.existsByEmail(request.email())).willReturn(false);
    given(userRepository.existsByUsername(request.username())).willReturn(false);
    given(userRepository.save(any(User.class))).willReturn(user);
    given(userMapper.toDto(any())).willReturn(userDto);

    UserDto result = userService.create(request, Optional.empty());

    assertThat(result.username()).isEqualTo("rex");
    then(userRepository).should(times(1)).save(any());
  }

  @Test
  @DisplayName("실패 - 중복된 이메일로 유저 생성")
  void createUser_fail_duplicateEmail() {
    UserCreateRequest request = new UserCreateRequest("rex", "rex@naver.com", "Rlawnsdn12!");
    given(userRepository.existsByEmail(request.email())).willReturn(true);

    assertThatThrownBy(() -> userService.create(request, Optional.empty()))
        .isInstanceOf(UserAlreadyExistException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_EMAIL);
  }

  @Test
  @DisplayName("성공 - 유저 삭제")
  void deleteUser_success() {
    UUID userId = UUID.randomUUID();
    given(userRepository.existsById(userId)).willReturn(true);

    userService.delete(userId);

    then(userRepository).should().deleteById(userId);
  }

  @Test
  @DisplayName("실패 - 존재하지 않는 유저")
  void deleteUser_fail_notfound() {
    UUID userId = UUID.randomUUID();
    given(userRepository.existsById(userId)).willReturn(false);

    assertThatThrownBy(() -> userService.delete(userId))
        .isInstanceOf(UserNotFoundException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CANNOT_FOUND_USER);
  }

  @Test
  @DisplayName("성공 - 유저 업데이트")
  void updateUser_success() {
    UUID userId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("newRex", "newRex@naver.com", "TestRex123!");
    User user = mock(User.class);

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userRepository.existsByEmail(request.newEmail())).willReturn(false);
    given(userRepository.existsByUsername(request.newUsername())).willReturn(false);
    given(userMapper.toDto(user)).willReturn(
        new UserDto(userId, request.newUsername(), request.newEmail(), null, true));

    UserDto update = userService.update(userId, request, Optional.empty());

    assertThat(update.username()).isEqualTo("newRex");
    then(user).should().update("newRex", "newRex@naver.com", "TestRex123!", null);
  }

  @Test
  @DisplayName("실패 - 유저 업데이트 중 중복 이름")
  void updateUser_fail_duplicateName() {
    UUID userId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("newRex", "newRex@naver.com", "TestRex123!");
    User oldUser = new User("rex", "rex@naver.com", "Rlawnsdn12!", null);

    given(userRepository.findById(userId)).willReturn(Optional.of(oldUser));
    given(userRepository.existsByEmail(request.newEmail())).willReturn(false);
    given(userRepository.existsByUsername(request.newUsername())).willReturn(true);

    assertThatThrownBy(() -> userService.update(userId, request, Optional.empty()))
        .isInstanceOf(UserAlreadyExistException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_NAME);
  }
}
