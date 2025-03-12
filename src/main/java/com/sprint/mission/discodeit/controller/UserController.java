package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusDTO;
import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.user.CreateUserRequest;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.user.UserDTO;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Log4j2
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;

  @Operation(summary = "Upload POST", description = "POST 방식으로 회원을 등록합니다")
  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<UserDTO> createUser(
          @Valid @RequestPart("userCreateRequest") CreateUserRequest userCreateRequest,
          @RequestPart(value = "profile", required = false) MultipartFile profile) {

    Optional<CreateBinaryContentRequest> profileRequest = Optional.empty();
    if (profile != null && !profile.isEmpty()) {
      try {
        profileRequest = Optional.of(new CreateBinaryContentRequest(
                profile.getOriginalFilename(),
                profile.getContentType(),
                profile.getBytes(),
                profile.getSize()
        ));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    UserDTO createdUser = userService.create(userCreateRequest, profileRequest);

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(createdUser);
  }

  @Operation(summary = "GET Users", description = "GET을 통해 전체 회원을 조회합니다")
  @GetMapping
  public ResponseEntity<List<UserDTO>> findAllUsers() {
    List<UserDTO> all = userService.findAll();
    return ResponseEntity.ok(all);
  }

  @Operation(summary = "Update Password", description = "회원의 비밀번호를 수정합니다")
  @PatchMapping(value = "/{userId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<UserDTO> updatePassword(@RequestParam("userId") UUID userId,
      @RequestPart("updateUserRequest") UpdateUserRequest updateUserRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {
    Optional<CreateBinaryContentRequest> profileRequest = Optional.empty();
    if (profile != null && !profile.isEmpty()) {
      try {
        profileRequest = Optional.of(new CreateBinaryContentRequest(
                profile.getOriginalFilename(),
                profile.getContentType(),
                profile.getBytes(),
                profile.getSize()
        ));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    UserDTO updateUser = userService.update(userId, updateUserRequest, profileRequest);

    return ResponseEntity.ok(updateUser);
  }

  @PatchMapping(path = "{userId}/userStatus")
  public ResponseEntity<UserStatusDTO> updateUserStatusByUserId(@PathVariable("userId") UUID userId) {
    UserStatusDTO updatedUserStatus = userStatusService.updateByUserId(userId);
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(updatedUserStatus);
  }

  @DeleteMapping
  public ResponseEntity<String> deleteUser(@RequestParam("id") UUID id) {
    userService.delete(id);
    return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
  }
}
