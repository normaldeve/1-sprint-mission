package com.sprint.mission.discodeit.security.session;

import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SessionRegistry {

  private final Map<UUID, HttpSession> sessions = new ConcurrentHashMap<>();

  public void registerSession(UUID userId, HttpSession session) {
    sessions.put(userId, session);
    log.info("✅ 세션 등록됨: userId={}, sessionId={}", userId, session.getId());
  }

  public void invalidateSession(UUID userId) {
    HttpSession session = sessions.remove(userId);
    if (session != null) {
      log.info("🚪 세션 무효화: userId={}, sessionId={}", userId, session.getId());
      session.invalidate();
    } else {
      log.info("⚠️ 세션 없음: userId={}", userId);
    }
  }
}
