package com.sprint.mission.discodeit.security.filter;

import com.sprint.mission.discodeit.security.session.SessionRegistry;
import com.sprint.mission.discodeit.security.user.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Remember-Me 인증으로 자동 로그인된 사용자를 세션 레지스트리에 등록하는 필터입니다.
 * 기존 세션 기반 로그인과 동일하게 온라인 상태 추적을 가능하게 하기 위해 사용됩니다.
 * isOnline() 메서드가 세션 기반으로 인식되기 때문에 클래스를 추가했습니다.
 *
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RememberMeSessionSynchronizationFilter extends OncePerRequestFilter {

  private final SessionRegistry sessionRegistry;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetails) {

      UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
      UUID userId = userDetails.getUser().getId();
      HttpSession session = request.getSession(false);

      if (session != null && !sessionRegistry.isUserOnline(userId)) {
        sessionRegistry.registerSession(userId, session);
        log.info("Remember-Me 사용자 세션 등록: userId={}, sessionId={}", userId, session.getId());
      }
    }

    filterChain.doFilter(request, response);

  }
}