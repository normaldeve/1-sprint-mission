package com.sprint.mission.discodeit.security.session;

import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * Servlet API에는 특정 사용자의 세션을 찾는 기능이 없어서 해당 클래스를 구현했습니다.
 * 동시 접속을 했을 경우 해당 사용자의 기존 세션을 무효화합니다.
 * isUserOnline() 메서드를 통해 현재 로그인 중인지 확인할 수 있음
 */
@Slf4j
@Component
public class SessionRegistry {

  private final Map<UUID, HttpSession> userSessions = new ConcurrentHashMap<>();

  public void registerSession(UUID userId, HttpSession session) {
    invalidateSession(userId);

    userSessions.put(userId, session);
    log.info("✅ 세션 등록됨: userId={}, sessionId={}", userId, session.getId());
  }

  public void invalidateSession(UUID userId) {
    HttpSession oldSession = userSessions.remove(userId);
    if (oldSession != null) {
      log.info("🚪 세션 무효화: userId={}, sessionId={}", userId, oldSession.getId());
      oldSession.invalidate();
    } else {
      log.info("⚠️ 세션 없음: userId={}", userId);
    }
  }

  public void unbindSession(HttpSession session) {
    userSessions.values().removeIf(s -> s.getId().equals(session.getId()));
  }

  public boolean isUserOnline(UUID userId) {
    HttpSession session = userSessions.get(userId);
    return session != null && session.getAttribute("SPRING_SECURITY_CONTEXT") != null;
  }
}