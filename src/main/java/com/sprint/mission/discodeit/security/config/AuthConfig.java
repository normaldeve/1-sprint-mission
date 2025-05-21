package com.sprint.mission.discodeit.security.config;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

@Configuration
public class AuthConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity http, DaoAuthenticationProvider provider) throws Exception {
    return http.getSharedObject(AuthenticationManagerBuilder.class)
        .authenticationProvider(provider)
        .build();
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
    hierarchy.setHierarchy("ROLE_ADMIN > ROLE_CHANNEL_MANAGER \n ROLE_CHANNEL_MANAGER > ROLE_USER");
    return hierarchy;
  }

  // 토큰을 저장할 수 있는 DB를 설정합니다.
  // 따로 엔티티를 생성하지는 않고 setCreateTableOnStartup을 통해 초기에 DB를 생성하는 방식을 채택
  @Bean
  public PersistentTokenRepository persistentTokenRepository(DataSource dataSource) {
    JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
//    tokenRepository.setCreateTableOnStartup(true);
    tokenRepository.setDataSource(dataSource);
    return tokenRepository;
  }

  // RememberMeServies 정의
  // 토큰을 브라우저에 저장하는 TokenBasedRememberMeServices보다 보안상 안정성이 높다고 판단
  @Bean
  public PersistentTokenBasedRememberMeServices rememberMeServices(
      UserDetailsService userDetailsService,
      PersistentTokenRepository persistentTokenRepository
  ) {
    PersistentTokenBasedRememberMeServices services = new PersistentTokenBasedRememberMeServices(
        "remember-me-key",
        userDetailsService,
        persistentTokenRepository
    );

    services.setTokenValiditySeconds(60 * 60 * 24 * 21); // 3주
    services.setParameter("remember-me");
    return services;
  }
}
