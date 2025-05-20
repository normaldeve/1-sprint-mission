package com.sprint.mission.discodeit.security.role;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 애플리케이션 실행 시 ROLE_ADMIN , ROLE_CHANNEL_MANAGER 권한을 가진 임의의 계정을 생성합니다
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminManagerInitializer implements ApplicationRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(ApplicationArguments args) {
    initializeAccount("admin", "admin@naver.com", "Rlawnsdn12!", Role.ROLE_ADMIN);
    initializeAccount("manager", "manager@naver.com", "Rlawnsdn12!", Role.ROLE_CHANNEL_MANAGER);
  }

  private void initializeAccount(String username, String email, String rawPassword, Role role) {
    // 데이터베이스에 해당하는 계정이 있다면 굳이 또 만들 필요는 없다
    if (userRepository.existsByUsername(username)) {
      return;
    }

    User user = User.builder()
        .username(username)
        .email(email)
        .password(passwordEncoder.encode(rawPassword))
        .role(role)
        .build();

    new UserStatus(user, Instant.now());
    userRepository.save(user);

    log.info("✅ {} 계정이 초기화되었습니다: {} / {}", role.name(), username, rawPassword);
  }
}
