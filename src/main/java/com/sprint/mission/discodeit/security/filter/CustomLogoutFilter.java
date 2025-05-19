package com.sprint.mission.discodeit.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 세션 기반 로그아웃 요청을 처리하는 커스텀 필터입니다.
 *
 * - /api/auth/logout 경로로 들어오는 POST 요청을 감지합니다.
 * - 해당 요청이 감지되면 현재 사용자의 세션을 무효화하고(SecurityContext 포함),
 *   인증 정보를 초기화하여 로그아웃 상태로 만듭니다.
 * - 그 외 요청은 필터 체인을 그대로 통과시킵니다.
 *
 */
@Slf4j
public class CustomLogoutFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    if (!request.getRequestURI().equals("/api/auth/logout") || !"POST".equalsIgnoreCase(
        request.getMethod())) {
      filterChain.doFilter(request, response);
      return;
    }

    log.info("👋 로그아웃 요청 수신");


    // 세션 무효화
    HttpSession session = request.getSession(false);
    if (session != null) {
      session.invalidate();
    }

    // SecurityContext 초기화
    SecurityContextHolder.clearContext();

    // 로그아웃 완료 응답
    response.setStatus(HttpServletResponse.SC_OK);

  }
}
