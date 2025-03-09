package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserDTO;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @PostMapping
  public ResponseEntity<UserDTO> createUser(
      @Valid @RequestPart("userCreateRequest") CreateUserRequest userCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {

    Optional<CreateBinaryContentRequest> profileRequest = Optional.empty();
    if (profile != null && !profile.isEmpty()) {
      try {
        profileRequest = Optional.of(new CreateBinaryContentRequest(
            profile.getOriginalFilename(),
            profile.getContentType(),
            profile.getBytes()
        ));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    UserDTO createdUser = userService.create(userCreateRequest, profileRequest);
    log.info(createdUser.getName() + "님 환영합니다. 회원가입이 완료되었습니다.");
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdUser);
  }

  @GetMapping
  public ResponseEntity<List<UserDTO>> findAllUsers() {
    List<UserDTO> all = userService.findAll();
    return ResponseEntity.ok(all);
  }

  @PatchMapping("/{userId}")
  public ResponseEntity<User> updatePassword(@RequestParam("userId") UUID userId,
      @RequestPart("updateUserRequest") UpdateUserRequest updateUserRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {
    Optional<CreateBinaryContentRequest> profileRequest = Optional.empty();
    if (profile != null && !profile.isEmpty()) {
      try {
        profileRequest = Optional.of(new CreateBinaryContentRequest(
            profile.getOriginalFilename(),
            profile.getContentType(),
            profile.getBytes()
        ));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    User updateUser = userService.update(userId, updateUserRequest, profileRequest);
    log.info("회원 수정이 완료되었습니다");
    return ResponseEntity.ok(updateUser);
  }

  @PatchMapping("/{userId}/userStatus")
  public ResponseEntity<UserStatus> updateUserStatus(@PathVariable("userId") UUID userId) {
    UserStatus userStatus = userStatusService.updateByUserId(userId);
    return ResponseEntity.ok(userStatus);
  }

  @DeleteMapping
  public ResponseEntity<String> deleteUser(@RequestParam("id") UUID id) {
    UserDTO delete = userService.delete(id);
    return ResponseEntity.ok(
        "Delete user ID: " + delete.getId() +
            " name: " + delete.getName() +
            " delete complete!"
    );
  }
}
