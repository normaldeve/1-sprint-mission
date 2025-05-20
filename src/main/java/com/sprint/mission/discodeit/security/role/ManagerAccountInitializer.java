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
 * 애플리케이션 실행 시 ROLE_CHANNEL_MANAGER 권한을 가진 임의의 계정을 생성합니다
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ManagerAccountInitializer implements ApplicationRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(ApplicationArguments args) {
    String managerUsername = "manager";
    String managerEmail = "manager@example.com";

    boolean exists = userRepository.existsByUsername(managerUsername);
    if (exists) {
      return;
    }

    User manager = User.builder()
        .username(managerUsername)
        .email(managerEmail)
        .password(passwordEncoder.encode("Rlawnsdn12!")) // 초기 비밀번호
        .role(Role.ROLE_CHANNEL_MANAGER) // 관리자 권한 부여
        .build();

    new UserStatus(manager, Instant.now());

    userRepository.save(manager);
    log.info("✅ ROLE_CHANNEL_MANAGER 계정이 초기화되었습니다: manager / Rlawnsdn12!");
  }
}
