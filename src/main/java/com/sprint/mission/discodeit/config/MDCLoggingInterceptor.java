package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class MDCLoggingInterceptor implements HandlerInterceptor {

  private static final String REQUEST_ID_HEADER = "Discodeit-Request-ID";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String requestId = UUID.randomUUID().toString();

    // MDC에 로그 추적용 정보 저장
    MDC.put("requestId", requestId);
    MDC.put("requestURL", request.getRequestURI());
    MDC.put("method", request.getMethod());

    // 응답 헤더에 요청 ID 추가
    response.setHeader(REQUEST_ID_HEADER, requestId);

    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    MDC.clear(); // 요청 처리 끝나면 MDC 클리어
  }
}

