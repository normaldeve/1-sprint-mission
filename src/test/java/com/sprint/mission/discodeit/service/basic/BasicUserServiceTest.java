package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;

@Import(BasicUserService.class)
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
  @DisplayName("프로필 없이 회원을 생성합니다")
  void createUser_withoutProfile() {
    //given
    UserCreateRequest request = new UserCreateRequest("junwo", "junwo@email.com", "password123");

    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(userRepository.existsByUsername(anyString())).thenReturn(false);

    User user = new User("junwo", "junwo@email.com", "password123", null);
    when(userRepository.save(any())).thenReturn(user);
    when(userMapper.toDto(any())).thenReturn(
        new UserDto(user.getId(), user.getUsername(), user.getEmail(), null, null)
    );

    //when
    UserDto result = userService.create(request, Optional.empty());

    //then
    assertThat(result).isNotNull();
    assertThat(result.username()).isEqualTo("junwo");

    verify(binaryContentRepository, never()).save(any());
    verify(binaryContentStorage, never()).put(any(), any());
  }

  @Test
  @DisplayName("프로필과 함께 회원을 생성합니다")
  void createUser_withProfile() {
    // given
    UserCreateRequest request = new UserCreateRequest("junwo", "junwo@email.com", "password123");

    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(userRepository.existsByUsername(anyString())).thenReturn(false);

    User user = new User("junwo", "junwo@email.com", "password123", null);
    when(userRepository.save(any())).thenReturn(user);
    when(userMapper.toDto(any())).thenReturn(
        new UserDto(user.getId(), user.getUsername(), user.getEmail(), null, null)
    );

    byte[] dummyBytes = "fake-image-bytes".getBytes();
    BinaryContentCreateRequest profileRequest = new BinaryContentCreateRequest(
        "profile.jpg", "image/jpeg", dummyBytes
    );

    // when
    UserDto result = userService.create(request, Optional.of(profileRequest));

    // then
    assertThat(result).isNotNull();
    assertThat(result.username()).isEqualTo("junwo");

    verify(binaryContentRepository).save(any());
    verify(binaryContentStorage).put(any(), any());
  }

  @Test
  @DisplayName("회원 생성 시 중복된 이메일이 존재하면 저장에 실패합니다")
  void createUser_duplicateEmail() {
    UserCreateRequest request = new UserCreateRequest("junwo", "junwo@email.com", "password123");

    when(userRepository.existsByEmail("junwo@email.com")).thenReturn(true);

    assertThatThrownBy(() -> userService.create(request, Optional.empty()))
        .isInstanceOf(UserAlreadyExistException.class);

    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("회원 생성 시 중복된 회원 이름이 존재하면 저장에 실패합니다")
  void createUser_duplicateUsername() {
    UserCreateRequest request = new UserCreateRequest("junwo", "junwo@email.com", "password123");

    when(userRepository.existsByUsername("junwo")).thenReturn(true);

    assertThatThrownBy(() -> userService.create(request, Optional.empty()))
        .isInstanceOf(UserAlreadyExistException.class);

    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("회원 조회 성공")
  void findUser_success() {
    // given
    UUID userId = UUID.randomUUID();
    User user = new User("junwo", "junwo@email.com", "password123", null);
    ReflectionTestUtils.setField(user, "id", userId);

    UserDto userDto = new UserDto(userId, user.getUsername(), user.getEmail(), null, null);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userMapper.toDto(user)).thenReturn(userDto);

    // when
    UserDto result = userService.find(userId);

    // then
    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(userId);
    assertThat(result.username()).isEqualTo("junwo");
  }

  @Test
  @DisplayName("회원 조회 실패 - 존재하지 않는 사용자")
  void findUser_notFound() {
    UUID userId = UUID.randomUUID();

    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.find(userId))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("전체 회원 목록 조회 성공")
  void findAllUsers() {
    // given
    User user1 = new User("junwo", "junwo@email.com", "pass1", null);
    User user2 = new User("mina", "mina@email.com", "pass2", null);

    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();

    ReflectionTestUtils.setField(user1, "id", id1);
    ReflectionTestUtils.setField(user2, "id", id2);

    when(userRepository.findAllWithProfileAndStatus()).thenReturn(List.of(user1, user2));
    when(userMapper.toDto(user1)).thenReturn(new UserDto(id1, "junwo", "junwo@email.com", null, null));
    when(userMapper.toDto(user2)).thenReturn(new UserDto(id2, "mina", "mina@email.com", null, null));

    // when
    List<UserDto> result = userService.findAll();

    // then
    assertThat(result).hasSize(2);
    assertThat(result).extracting("username").containsExactly("junwo", "mina");
  }

  @Test
  @DisplayName("회원 수정 - 프로필 없이 성공")
  void updateUser_withoutProfile() {
    // given
    UUID userId = UUID.randomUUID();
    User user = new User("oldName", "old@email.com", "oldPassword", null);
    ReflectionTestUtils.setField(user, "id", userId);

    UserUpdateRequest request = new UserUpdateRequest("newName", "new@email.com", "newPassword");

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userRepository.existsByEmail("new@email.com")).thenReturn(false);
    when(userRepository.existsByUsername("newName")).thenReturn(false);
    when(userMapper.toDto(any())).thenReturn(new UserDto(userId, "newName", "new@email.com", null, null));

    // when
    UserDto result = userService.update(userId, request, Optional.empty());

    // then
    assertThat(result).isNotNull();
    assertThat(result.username()).isEqualTo("newName");

    verify(binaryContentRepository, never()).save(any());
    verify(binaryContentStorage, never()).put(any(), any());
  }

  @Test
  @DisplayName("회원 수정 - 프로필 포함 성공")
  void updateUser_withProfile_success() {
    // given
    UUID userId = UUID.randomUUID();
    User user = new User("oldName", "old@email.com", "oldPassword", null);
    ReflectionTestUtils.setField(user, "id", userId);

    byte[] profileBytes = "test-image".getBytes();
    BinaryContentCreateRequest profileRequest = new BinaryContentCreateRequest("profile.png", "image/png", profileBytes);

    UserUpdateRequest request = new UserUpdateRequest("newName", "new@email.com", "newPassword");

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userRepository.existsByEmail("new@email.com")).thenReturn(false);
    when(userRepository.existsByUsername("newName")).thenReturn(false);
    when(userMapper.toDto(any())).thenReturn(new UserDto(userId, "newName", "new@email.com", null, null));

    // when
    UserDto result = userService.update(userId, request, Optional.of(profileRequest));

    // then
    assertThat(result).isNotNull();
    assertThat(result.username()).isEqualTo("newName");

    verify(binaryContentRepository).save(any());
    verify(binaryContentStorage).put(any(), any());
  }

  @Test
  @DisplayName("중복된 이메일로는 업데이트 할 수 없습니다")
  void updateUser_duplicateEmail() {
    // given
    UUID userId = UUID.randomUUID();
    User user = new User("oldName", "old@email.com", "oldPassword", null);
    ReflectionTestUtils.setField(user, "id", userId);

    UserUpdateRequest request = new UserUpdateRequest("newName", "dupe@email.com", "newPassword");

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userRepository.existsByEmail("dupe@email.com")).thenReturn(true);

    // when + then
    assertThatThrownBy(() -> userService.update(userId, request, Optional.empty()))
        .isInstanceOf(UserAlreadyExistException.class);
  }

  @Test
  @DisplayName("중복된 회원 이름으로는 업데이트 할 수 없습니다")
  void updateUser_duplicateUsername() {
    // given
    UUID userId = UUID.randomUUID();
    User user = new User("oldName", "old@email.com", "oldPassword", null);
    ReflectionTestUtils.setField(user, "id", userId);

    UserUpdateRequest request = new UserUpdateRequest("newName", "dupe@email.com", "newPassword");

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userRepository.existsByUsername("newName")).thenReturn(true);

    // when + then
    assertThatThrownBy(() -> userService.update(userId, request, Optional.empty()))
        .isInstanceOf(UserAlreadyExistException.class);
  }

  @Test
  @DisplayName("회원 수정 실패 - 사용자 없음")
  void updateUser_userNotFound() {
    UUID userId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("newName", "new@email.com", "newPassword");

    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.update(userId, request, Optional.empty()))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("회원 삭제 - 성공")
  void deleteUser_success() {
    // given
    UUID userId = UUID.randomUUID();

    when(userRepository.existsById(userId)).thenReturn(true);

    // when
    userService.delete(userId);

    verify(userRepository).deleteById(userId);
  }

  @Test
  @DisplayName("회원 삭제 - 실패 (존재하지 않는 사용자)")
  void deleteUser_userNotFound_fail() {
    UUID userId = UUID.randomUUID();
    when(userRepository.existsById(userId)).thenReturn(false);

    assertThatThrownBy(() -> userService.delete(userId))
        .isInstanceOf(UserNotFoundException.class);

    verify(userRepository, never()).deleteById(userId);
  }
}