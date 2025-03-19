package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.login.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserDTO;
import com.sprint.mission.discodeit.service.basic.BasicAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
//
//  private final BasicAuthService authService;
//
//  @PostMapping("/login")
//  public ResponseEntity<UserDTO> login(@RequestBody LoginRequest request) {
//    UserDTO login = authService.login(request);
//    log.info(request.username() + "님 환영합니다~. 로그인이 완료되었습니다");
//    return ResponseEntity
//        .status(HttpStatus.OK)
//        .body(login);
//  }
}
