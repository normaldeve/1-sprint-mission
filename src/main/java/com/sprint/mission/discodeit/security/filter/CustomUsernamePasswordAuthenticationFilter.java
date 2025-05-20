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
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
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
 * JSON 기반 로그인 요청을 처리하는 커스텀 인증 필터 UsernamePasswordAuthenticationFilter를 확장하여 /api/auth/login 엔드포인트에서
 * JSON 본문으로 전달된 사용자명과 비밀번호를 추출하여 인증을 시도합니다.
 */
@Slf4j
public class CustomUsernamePasswordAuthenticationFilter extends
    UsernamePasswordAuthenticationFilter {

  private ObjectMapper objectMapper = new ObjectMapper();
  private SessionRegistry sessionRegistry;
  private PersistentTokenBasedRememberMeServices rememberMeServices;
  private PersistentTokenRepository tokenRepository;

  /**
   * 생성자에서 인증 매니저를 설정하고 처리할 로그인 URL를 지정합니다.
   *
   * @param authenticationManager
   */
  public CustomUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager,
      SessionRegistry sessionRegistry, PersistentTokenBasedRememberMeServices rememberMeServices,
      PersistentTokenRepository tokenRepository) {

    super.setAuthenticationManager(authenticationManager);
    this.sessionRegistry = sessionRegistry;
    this.rememberMeServices = rememberMeServices;
    this.tokenRepository = tokenRepository;
    setFilterProcessesUrl("/api/auth/login");
  }

  /**
   * 클라이언트로부터 들어온 JSON을 파싱하여 UsernamePasswordAuthenticationToken 생성 후 인증을 시도합니다.
   *
   * @param request
   * @param response
   * @return
   * @throws AuthenticationException
   */
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {
    try {
      log.info("📥 로그인 요청 수신");

      LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(),
          LoginRequest.class);

      log.info("🔐 로그인 요청 - username: {}, password: {}**",
          loginRequest.username(),
          loginRequest.password().substring(0, Math.min(2, loginRequest.password().length())));


      if (Boolean.TRUE.equals(loginRequest.rememberMe())) {
        request.setAttribute("remember-me", true);
      }

      UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
          loginRequest.username(), loginRequest.password());

      log.info("🔄 인증 매니저에 전달");

      return this.getAuthenticationManager().authenticate(authenticationToken);
    } catch (IOException e) {
      throw new AuthenticationServiceException("잘못된 로그인 요청입니다. 이름과 비밀번호를 확인해주세요", e);
    }
  }

  /**
   * 인증 성공 시 사용자 정보를 JSON 형식으로 응답. SecurityContext 저장은 SecurityContextRepository가 담당.
   */
  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult) throws IOException {
    log.info("✅ 인증 성공: principal = {}", authResult.getPrincipal());
    log.info("✅ 로그인 사용자 권한 목록: {}", authResult.getAuthorities());

    UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
    UUID userId = userDetails.getUser().getId();
    String username = userDetails.getUsername();
    HttpSession session = request.getSession(true);

    log.info("🔑 로그인 성공 - 세션 ID: {}", session.getId());

    //동일 사용자로 등록된 기존에 세션을 삭제합니다.
    sessionRegistry.invalidateSession(userId);

    // 기존 remember-me 토큰을 삭제합니다. (중복 로그인 시 재인증 방지)
    tokenRepository.removeUserTokens(username);

    // 새 세션을 등록합니다.
    sessionRegistry.registerSession(userId, session);

    // SecurityContext 저장
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authResult);
    SecurityContextHolder.setContext(context);
    request.getSession(true)
        .setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

    // 로그인 응답
    UserDetailsImpl userPrincipal = (UserDetailsImpl) authResult.getPrincipal();
    LoginResponse responseDto = new LoginResponse(userPrincipal.getUser().getId(),
        userPrincipal.getUsername(),
        userPrincipal.getUser().getEmail());
    response.setContentType("application/json");
    response.getWriter().write(objectMapper.writeValueAsString(responseDto));

    rememberMeServices.loginSuccess(request, response, authResult);
  }

  /**
   * 인증 실패 시 401 상태 코드와 함께 오류 메시지를 JSON 형식으로 응답.
   */
  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException failed) throws IOException {
    log.warn("❌ 인증 실패 - 사유: {}", failed.getMessage());
    ErrorResponse error = new ErrorResponse("Invalid username or password");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");
    response.getWriter().write(new ObjectMapper().writeValueAsString(error));
  }
}
