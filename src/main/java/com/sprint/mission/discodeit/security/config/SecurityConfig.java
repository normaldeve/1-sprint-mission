package com.sprint.mission.discodeit.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.security.filter.RememberMeSessionSynchronizationFilter;
import com.sprint.mission.discodeit.security.session.SessionRegistry;
import com.sprint.mission.discodeit.security.filter.CustomLogoutFilter;
import com.sprint.mission.discodeit.security.filter.CustomUsernamePasswordAuthenticationFilter;
import com.sprint.mission.discodeit.security.handler.CustomAccessDeniedHandler;
import com.sprint.mission.discodeit.security.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RememberMeConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer.SessionFixationConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {

  private final ObjectMapper objectMapper;
  private final SessionRegistry sessionRegistry;
  private final PersistentTokenBasedRememberMeServices rememberMeServices;
  private final AuthenticationManager authenticationManager;
  private final PersistentTokenRepository tokenRepository;

  /**
   * API 엔드포인트 경로 상수
   */
  private static class ApiEndpoints {
    // 인증 관련 엔드포인트
    private static final String AUTH_ME = "/api/auth/me";
    private static final String AUTH_CSRF_TOKEN = "/api/auth/csrf-token";
    private static final String AUTH_ROLE = "/api/auth/role";

    // 사용자 관련 엔드포인트
    private static final String USERS = "/api/users";

    // 채널 관련 엔드포인트
    private static final String CHANNELS_PUBLIC = "/api/channels/public";
    private static final String CHANNELS_ALL = "/api/channels/**";
  }

  /**
   * 정적 리소스 경로 상수
   */
  private static class StaticResources {
    private static final String[] SWAGGER_RESOURCES = {
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/swagger-resources/**"
    };

    private static final String[] PUBLIC_RESOURCES = {
        "/actuator/**",
        "/favicon.ico",
        "/",
        "/assets/**",
        "/index.html"
    };
  }

  /**
   * 권한 상수
   */
  private static class Roles {
    private static final String USER = "USER";
    private static final String ADMIN = "ADMIN";
    private static final String CHANNEL_MANAGER = "CHANNEL_MANAGER";

    private static String hasRole(String role) {
      return "ROLE_" + role;
    }
  }

  /**
   * 설정 상수
   */
  private static class SecurityConstants {
    private static final String REMEMBER_ME_KEY = "remember-me-key";
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    log.info("✅ SecurityFilterChain 구성 시작");

    CustomUsernamePasswordAuthenticationFilter loginFilter =
        new CustomUsernamePasswordAuthenticationFilter(authenticationManager, sessionRegistry,
            rememberMeServices, tokenRepository, objectMapper);

    loginFilter.setSecurityContextRepository(new HttpSessionSecurityContextRepository());

    CustomLogoutFilter logoutFilter = new CustomLogoutFilter(tokenRepository, sessionRegistry);

    // 인증/인가 실패 처리기 생성
    CustomAuthenticationEntryPoint authenticationEntryPoint = new CustomAuthenticationEntryPoint(objectMapper);
    CustomAccessDeniedHandler accessDeniedHandler = new CustomAccessDeniedHandler(objectMapper);

    http
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())) // CustomCsrfToken 사용
        .logout(AbstractHttpConfigurer::disable) // CustomLogoutFilter 사용
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                ApiEndpoints.AUTH_ME,
                ApiEndpoints.AUTH_CSRF_TOKEN
            ).permitAll()
            .requestMatchers(StaticResources.SWAGGER_RESOURCES).permitAll()
            .requestMatchers(StaticResources.PUBLIC_RESOURCES).permitAll()
            .requestMatchers(HttpMethod.POST, ApiEndpoints.USERS).permitAll()
            .requestMatchers(HttpMethod.POST, ApiEndpoints.CHANNELS_PUBLIC)
            .hasRole(Roles.CHANNEL_MANAGER)
            .requestMatchers(HttpMethod.PATCH, ApiEndpoints.CHANNELS_ALL)
            .hasRole(Roles.CHANNEL_MANAGER)
            .requestMatchers(HttpMethod.DELETE, ApiEndpoints.CHANNELS_ALL)
            .hasRole(Roles.CHANNEL_MANAGER)
            .requestMatchers(HttpMethod.PUT, ApiEndpoints.AUTH_ROLE).hasRole(Roles.ADMIN)
            .anyRequest().hasRole(Roles.USER)
        )
        // 인증/인가 실패 처리 설정
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint(authenticationEntryPoint)  // 인증되지 않은 사용자
            .accessDeniedHandler(accessDeniedHandler)           // 권한 없는 사용자
        )
        .addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(logoutFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterAt(new RememberMeSessionSynchronizationFilter(sessionRegistry),
            RememberMeAuthenticationFilter.class)
        .rememberMe(this::configureRememberMe)
        .sessionManagement(session -> session
            .sessionFixation(SessionFixationConfigurer::migrateSession) // 세션 고정 공격 방지
            .maximumSessions(1)                                         // 동시 로그인 1회 제한
            .maxSessionsPreventsLogin(true)                             // 중복 로그인 시 새 로그인 차단
        );

    return http.build();
  }

  private void configureRememberMe(RememberMeConfigurer<HttpSecurity> remember) {
    remember
        .rememberMeServices(rememberMeServices)
        .key(SecurityConstants.REMEMBER_ME_KEY);
  }
}