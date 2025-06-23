package com.sprint.mission.discodeit.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import com.sprint.mission.discodeit.dto.response.LoginResponse;
import com.sprint.mission.discodeit.security.session.SessionRegistry;
import com.sprint.mission.discodeit.security.user.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

/**
 * JSON 기반 로그인 요청을 처리하는 커스텀 인증 필터
 * UsernamePasswordAuthenticationFilter를 확장하여 /api/auth/login 엔드포인트에서
 * JSON 본문으로 전달된 사용자명과 비밀번호를 추출하여 인증을 시도합니다.
 */
@Slf4j
public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final ObjectMapper objectMapper;
  private final SessionRegistry sessionRegistry;
  private final PersistentTokenBasedRememberMeServices rememberMeServices;
  private final PersistentTokenRepository tokenRepository;
  private static final String LOGIN_ENDPOINT = "/api/auth/login";
  private static final String REMEMBER_ME_ATTRIBUTE = "remember-me";

  /**
   * 생성자에서 필요한 의존성을 주입받고 처리할 로그인 URL를 지정합니다.
   *
   * @param authenticationManager 인증을 처리할 매니저
   * @param sessionRegistry 세션 관리 레지스트리
   * @param rememberMeServices Remember Me 서비스
   * @param tokenRepository 토큰 저장소
   * @param objectMapper JSON 직렬화/역직렬화 매퍼
   */
  public CustomUsernamePasswordAuthenticationFilter(
      AuthenticationManager authenticationManager,
      SessionRegistry sessionRegistry,
      PersistentTokenBasedRememberMeServices rememberMeServices,
      PersistentTokenRepository tokenRepository,
      ObjectMapper objectMapper) {

    // null 체크
    Objects.requireNonNull(authenticationManager, "AuthenticationManager must not be null");
    Objects.requireNonNull(sessionRegistry, "SessionRegistry must not be null");
    Objects.requireNonNull(rememberMeServices, "RememberMeServices must not be null");
    Objects.requireNonNull(tokenRepository, "TokenRepository must not be null");
    Objects.requireNonNull(objectMapper, "ObjectMapper must not be null");

    super.setAuthenticationManager(authenticationManager);
    this.sessionRegistry = sessionRegistry;
    this.rememberMeServices = rememberMeServices;
    this.tokenRepository = tokenRepository;
    this.objectMapper = objectMapper;
    setFilterProcessesUrl(LOGIN_ENDPOINT);
  }

  /**
   * 클라이언트로부터 들어온 JSON을 파싱하여 UsernamePasswordAuthenticationToken 생성 후 인증을 시도합니다.
   *
   * @param request HTTP 요청
   * @param response HTTP 응답
   * @return 인증 결과
   * @throws AuthenticationException 인증 과정에서 발생한 예외
   */
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {
    if (!request.getMethod().equals("POST")) {
      throw new AuthenticationServiceException("인증 메서드가 지원되지 않습니다: " + request.getMethod());
    }

    try {
      log.info("📥 로그인 요청 수신");

      LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(),
          LoginRequest.class);

      validateLoginRequest(loginRequest);

      // 안전한 로깅을 위해 패스워드는 첫 2자 이하만 표시
      log.info("🔐 로그인 요청 - username: {}, password: {}**",
          loginRequest.username(),
          maskPassword(loginRequest.password()));

      if (Boolean.TRUE.equals(loginRequest.rememberMe())) {
        request.setAttribute(REMEMBER_ME_ATTRIBUTE, true);
      }

      UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
          loginRequest.username(), loginRequest.password());

      log.info("🔄 인증 매니저에 전달");

      return this.getAuthenticationManager().authenticate(authenticationToken);
    } catch (IOException e) {
      log.error("로그인 요청 파싱 중 오류 발생", e);
      throw new AuthenticationServiceException("잘못된 로그인 요청 형식입니다", e);
    } catch (IllegalArgumentException e) {
      log.error("로그인 요청 데이터 검증 중 오류 발생", e);
      throw new BadCredentialsException("로그인 정보가 올바르지 않습니다", e);
    }
  }

  /**
   * 로그인 요청의 유효성을 검증합니다.
   *
   * @param loginRequest 로그인 요청 객체
   * @throws IllegalArgumentException 유효하지 않은 요청일 경우 발생
   */
  private void validateLoginRequest(LoginRequest loginRequest) {
    if (loginRequest == null) {
      throw new IllegalArgumentException("로그인 요청이 비어있습니다");
    }
    if (loginRequest.username() == null || loginRequest.username().isBlank()) {
      throw new IllegalArgumentException("사용자 이름이 비어있습니다");
    }
    if (loginRequest.password() == null || loginRequest.password().isBlank()) {
      throw new IllegalArgumentException("비밀번호가 비어있습니다");
    }
  }

  /**
   * 패스워드를 마스킹 처리하여 반환합니다.
   *
   * @param password 원본 패스워드
   * @return 마스킹된 패스워드 (첫 2자 이하만 표시)
   */
  private String maskPassword(String password) {
    if (password == null || password.isEmpty()) {
      return "";
    }
    return password.substring(0, Math.min(2, password.length()));
  }

  /**
   * 인증 성공 시 사용자 정보를 JSON 형식으로 응답합니다.
   * SecurityContext 저장은 SecurityContextRepository가 담당합니다.
   */
  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authentication) throws IOException {
    log.info("✅ 인증 성공: principal = {}", authentication.getName());
    log.info("✅ 로그인 사용자 권한 목록: {}", authentication.getAuthorities());

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    UUID userId = userDetails.getUser().getId();
    String username = userDetails.getUsername();
    HttpSession session = request.getSession(true);

    log.info("🔑 로그인 성공 - 세션 ID: {}", session.getId());

    try {
      // 동일 사용자로 등록된 기존에 세션을 삭제합니다.
      sessionRegistry.invalidateSession(userId);
      log.debug("기존 세션 무효화 완료: userId={}", userId);

      // 기존 remember-me 토큰을 삭제합니다. (중복 로그인 시 재인증 방지)
      tokenRepository.removeUserTokens(username);
      log.debug("기존 remember-me 토큰 삭제 완료: username={}", username);

      // 새 세션을 등록합니다.
      sessionRegistry.registerSession(userId, session);
      log.debug("새 세션 등록 완료: sessionId={}, userId={}", session.getId(), userId);

      // SecurityContext 저장
      SecurityContext context = SecurityContextHolder.createEmptyContext();
      context.setAuthentication(authentication);
      SecurityContextHolder.setContext(context);
      session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
      log.debug("SecurityContext 저장 완료");

      // 로그인 응답
      UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
      LoginResponse responseDto = new LoginResponse(
          userPrincipal.getUser().getId(),
          userPrincipal.getUsername(),
          userPrincipal.getUser().getEmail()
      );

      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      response.getWriter().write(objectMapper.writeValueAsString(responseDto));

      // Remember Me 서비스 호출
      rememberMeServices.loginSuccess(request, response, authentication);
      log.debug("Remember Me 서비스 처리 완료");
    } catch (Exception e) {
      // 인증은 성공했지만 세션 처리 중 오류 발생
      log.error("인증 후처리 중 오류 발생", e);
      sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          "로그인은 성공했으나 세션 처리 중 오류가 발생했습니다");
    }
  }

  /**
   * 인증 실패 시 401 상태 코드와 함께 오류 메시지를 JSON 형식으로 응답합니다.
   */
  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException failed) throws IOException {
    // 민감한 정보가 노출되지 않도록 일반적인 오류 메시지 사용
    log.warn("❌ 인증 실패 - 유형: {}", failed.getClass().getSimpleName());

    // 보안을 위해 구체적인 실패 원인은 로그에만 남기고 응답에는 포함하지 않음
    if (log.isDebugEnabled()) {
      log.debug("인증 실패 상세 사유: {}", failed.getMessage(), failed);
    }

    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "사용자 이름 또는 비밀번호가 올바르지 않습니다");
  }

  /**
   * 오류 응답을 JSON 형식으로 전송합니다.
   *
   * @param response HTTP 응답 객체
   * @param status HTTP 상태 코드
   * @param message 오류 메시지
   * @throws IOException 응답 작성 중 오류 발생 시
   */
  private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
    ErrorResponse error = new ErrorResponse(message);
    response.setStatus(status);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(objectMapper.writeValueAsString(error));
  }
}