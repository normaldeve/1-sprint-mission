package com.sprint.mission.discodeit.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.ErrorResponse;
import com.sprint.mission.discodeit.dto.response.LoginResponse;
import com.sprint.mission.discodeit.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * JSON 기반 로그인 요청을 처리하는 커스텀 인증 필터
 * UsernamePasswordAuthenticationFilter를 확장하여
 * /api/auth/login 엔드포인트에서 JSON 본문으로 전달된 사용자명과 비밀번호를 추출하여 인증을 시도합니다.
 */
public class CustomUsernamePasswordAuthenticationFilter extends
    UsernamePasswordAuthenticationFilter {

  private ObjectMapper objectMapper = new ObjectMapper();

  /**
   * 생성자에서 인증 매니저를 설정하고 처리할 로그인 URL를 지정합니다.
   *
   * @param authenticationManager
   */
  public CustomUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
    super.setAuthenticationManager(authenticationManager);
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
      LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(),
          LoginRequest.class);
      UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
          loginRequest.username(), loginRequest.password());

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
      FilterChain chain, Authentication authResult) throws IOException, ServletException {
    User user = (User) authResult.getPrincipal();
    LoginResponse responseDto = new LoginResponse(user.getId(), user.getUsername(),
        user.getEmail());
    response.setContentType("application/json");
    response.getWriter().write(objectMapper.writeValueAsString(responseDto));
  }

  /**
   * 인증 실패 시 401 상태 코드와 함께 오류 메시지를 JSON 형식으로 응답.
   */
  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException failed) throws IOException {
    ErrorResponse error = new ErrorResponse("Invalid username or password");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");
    response.getWriter().write(new ObjectMapper().writeValueAsString(error));
  }
}
