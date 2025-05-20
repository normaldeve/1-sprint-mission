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
 * 애플리케이션 실행 시 ROLE_ADMIN 권한을 가진 임의의 계정을 생성합니다
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements ApplicationRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(ApplicationArguments args) {
    String adminUsername = "admin";
    String adminEmail = "admin@example.com";

    boolean exists = userRepository.existsByUsername(adminUsername);
    if (exists) {
      return;
    }

    User admin = User.builder()
        .username(adminUsername)
        .email(adminEmail)
        .password(passwordEncoder.encode("admin1234")) // 초기 비밀번호
        .role(Role.ROLE_ADMIN) // 관리자 권한 부여
        .build();

    new UserStatus(admin, Instant.now());

    userRepository.save(admin);
    log.info("✅ ROLE_ADMIN 계정이 초기화되었습니다: admin / admin1234!");
  }
}
