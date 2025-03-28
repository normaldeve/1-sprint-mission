package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("dev")
@EnableJpaAuditing
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserStatusRepository userStatusRepository;

  @Test
  @DisplayName("User 생성 시 createdAt 자동 생성")
  void createUser_shouldSetTimestamps() {
    User user = new User("rex", "rex@naver.com", "Rlawnsdn12!", null);
    User save = userRepository.save(user);

    assertThat(save.getCreatedAt()).isNotNull();
    assertThat(save.getUpdatedAt()).isNotNull();
  }

  @Test
  @DisplayName("사용자 저장 및 username으로 조회 성공")
  void findByUsername_success() {
    User user = new User("rex", "rex@naver.com", "Rlawnsdn12!", null);
    userRepository.save(user);

    Optional<User> findUser = userRepository.findByUsername("rex");

    assertThat(findUser).isPresent();
  }


  @Test
  @DisplayName("existsByEmail, existsByUsername 테스트")
  void existsByEmailAndUsername_success() {
    // given
    User user = new User("junwo", "junwo@email.com", "password123!", null);
    userRepository.save(user);

    // when
    boolean emailExists = userRepository.existsByEmail("junwo@email.com");
    boolean usernameExists = userRepository.existsByUsername("junwo");

    // then
    assertThat(emailExists).isTrue();
    assertThat(usernameExists).isTrue();
  }

  @Test
  @DisplayName("사용자와 프로필, 상태까지 함께 조회 (fetch join)")
  void findAllWithProfileAndStatus_success() {
    // given
    User user = new User("junwo", "junwo@email.com", "password123!", null);
    userRepository.save(user);

    UserStatus status = new UserStatus(user, Instant.now());
    userStatusRepository.save(status);

    // when
    List<User> users = userRepository.findAllWithProfileAndStatus();

    // then
    assertThat(users).isNotNull();
    assertThat(users.get(0).getStatus()).isNotNull(); // fetch join 확인
  }
}
