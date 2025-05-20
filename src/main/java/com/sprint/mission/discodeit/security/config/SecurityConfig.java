package com.sprint.mission.discodeit.security.config;

import com.sprint.mission.discodeit.security.filter.RememberMeSessionSynchronizationFilter;
import com.sprint.mission.discodeit.security.session.SessionRegistry;
import com.sprint.mission.discodeit.security.filter.CustomLogoutFilter;
import com.sprint.mission.discodeit.security.filter.CustomUsernamePasswordAuthenticationFilter;
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

@Configuration
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {

  private final SessionRegistry sessionRegistry;
  private final PersistentTokenBasedRememberMeServices rememberMeServices;
  private final AuthenticationManager authenticationManager;
  private final PersistentTokenRepository tokenRepository;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    log.info("✅ SecurityFilterChain 구성 시작");

    CustomUsernamePasswordAuthenticationFilter loginFilter =
        new CustomUsernamePasswordAuthenticationFilter(authenticationManager, sessionRegistry,
            rememberMeServices, tokenRepository);

    loginFilter.setSecurityContextRepository(new HttpSessionSecurityContextRepository());

    CustomLogoutFilter logoutFilter = new CustomLogoutFilter(tokenRepository, sessionRegistry);

    http
        .csrf(AbstractHttpConfigurer::disable)
        .logout(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/api/auth/me", "/api/auth/csrf-token",
                "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**",
                "/actuator/**", "/favicon.ico",
                "/", "/assets/**", "/index.html"
            ).permitAll()
            .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/channels/public").hasRole("CHANNEL_MANAGER")
            .requestMatchers(HttpMethod.PATCH, "/api/channels/**").hasRole("CHANNEL_MANAGER")
            .requestMatchers(HttpMethod.DELETE, "/api/channels/**").hasRole("CHANNEL_MANAGER")
            .requestMatchers(HttpMethod.PUT, "/api/auth/role").hasRole("ADMIN")
            .anyRequest().hasRole("USER")
        )
        .addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(logoutFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterAt(new RememberMeSessionSynchronizationFilter(sessionRegistry),
            RememberMeAuthenticationFilter.class)
        .rememberMe(this::configureRememberMe)
        .sessionManagement(session -> session
            .sessionFixation(SessionFixationConfigurer::migrateSession));

    return http.build();
  }

  private void configureRememberMe(RememberMeConfigurer<HttpSecurity> remember) {
    remember
        .rememberMeServices(rememberMeServices)
        .key("remember-me-key");
  }

}