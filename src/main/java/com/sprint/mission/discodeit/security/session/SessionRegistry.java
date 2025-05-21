package com.sprint.mission.discodeit.security.session;

import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * registerSession()은 이전 세션을 강제로 invalidate() 하여 기존 세션을 강제 로그아웃 시킴
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
