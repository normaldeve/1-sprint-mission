package com.sprint.mission.discodeit.security.config;

import com.sprint.mission.discodeit.security.filter.CustomUsernamePasswordAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

@Slf4j
@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
    CustomUsernamePasswordAuthenticationFilter loginFilter = new CustomUsernamePasswordAuthenticationFilter(
        authenticationManager);

    loginFilter.setSecurityContextRepository(new HttpSessionSecurityContextRepository());

    http
        .logout(AbstractHttpConfigurer::disable) // logoutfilter는 사용하지 않습니다.
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/api/auth/csrf-token", "/api/users",
                "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**",
                "/actuator/**", "/favicon.ico",
                "/", "/assets/**", "/index.html"
            ).permitAll()
            .anyRequest().authenticated()
        )
        .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Spring Security 기본 인증 방식인 DaoAuthenticationProvider 빈 등록하기
   * UserDetailsService를 통해 사용자 정보 조회, PasswordEncoder로 비밀번호를 검증함
   */
  @Bean
  public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity http, DaoAuthenticationProvider provider) throws Exception {
    return http.getSharedObject(AuthenticationManagerBuilder.class)
        .authenticationProvider(provider)
        .build();
  }
}
