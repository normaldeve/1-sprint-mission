package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.security.user.UserDetailsImpl;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final UserMapper userMapper;
  private final UserService userService;

  @GetMapping("/csrf-token")
  public CsrfToken getCsrfToken(HttpServletRequest request) {
    log.info("csrf-token: {}", request.getHeader("X-CSRF-TOKEN"));
    return (CsrfToken) request.getAttribute(CsrfToken.class.getName());
  }

  /**
   * 현재 인증된 사용자 정보를 반환하는 API 세션에 저장된 SecurityContext에서 Authentication -> Principal -> User 반환
   */
  @GetMapping("/me")
  public ResponseEntity<UserDto> me(@AuthenticationPrincipal UserDetailsImpl userPrincipal) {
    if (userPrincipal == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    UUID userId = userPrincipal.getUser().getId();
    UserDto userDto = userService.getCurrentUser(userId);

    return ResponseEntity.ok(userDto);
  }

  /**
   * 사용자 권한을 수정합니다. 권한이 수정된 사용자가 로그인 상태이면 강제 로그아웃 됩니다.
   */
  @PutMapping("/role")
  public ResponseEntity<UserDto> updateUserRole(
      @RequestBody UserRoleUpdateRequest request
  ) {
    UserDto userDto = userService.updateUserRole(request);

    return ResponseEntity.ok(userDto);
  }
}
