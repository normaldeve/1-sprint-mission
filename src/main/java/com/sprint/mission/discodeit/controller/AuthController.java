package com.sprint.mission.discodeit.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @GetMapping("/csrf-token")
  public CsrfToken getCsrfToken(HttpServletRequest request) {
    log.info("csrf-token: {}", request.getHeader("X-CSRF-TOKEN"));
    return (CsrfToken) request.getAttribute(CsrfToken.class.getName());
  }
}
