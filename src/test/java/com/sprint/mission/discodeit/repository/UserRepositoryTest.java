package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.List;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
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

  @Autowired
  private TestEntityManager em;

  @Test
  @DisplayName("User 생성 시 createdAt와 updatedAt은 자동 생성된다")
  void createUser_shouldSetTimestamps() {
    User user = userRepository.save(new User("test", "test@naver.com", "testPassword12!", null));
    em.flush();
    em.clear();

    User findUser = userRepository.findById(user.getId()).orElse(null);

    assertThat(findUser.getCreatedAt()).isNotNull();
    assertThat(findUser.getUpdatedAt()).isNotNull();
  }

  @Test
  @DisplayName("회원의 이름을 통해 DB에 회원이 존재하는지 확인할 수 있다.")
  void existsByEmailAndUsername_success() {
    User user = new User("junwo", "junwo@email.com", "password123!", null);
    userRepository.save(user);

    boolean usernameExists = userRepository.existsByUsername("junwo");

    assertThat(usernameExists).isTrue();
  }

  @Test
  @DisplayName("이메일 주소를 통해서 회원이 DB에 존재하는지 확인할 수 있다.")
  void existsByEmail_success() {
    User user = new User("junwo", "junwo@email.com", "password123!", null);
    userRepository.save(user);

    boolean emailExists = userRepository.existsByEmail("junwo@email.com");

    assertThat(emailExists).isTrue();
  }

  @Test
  @DisplayName("User 조회 시 Profile, UserStatus가 함께 조회된다")
  void findAllProfileAndStatus_success() {
    BinaryContent profile = new BinaryContent("userProfile", 10L, ".jpeg");
    User user = userRepository.save(new User("test", "test@naver.com", "testPassword12!", profile));
    UserStatus userStatus = new UserStatus(user, Instant.now());
    em.flush();
    em.clear();

    List<User> users = userRepository.findAllWithProfileAndStatus();

    assertThat(users.size()).isEqualTo(1);
    User findUser = users.get(0);
    assertThat(findUser.getProfile().getId()).isEqualTo(profile.getId());
    assertThat(findUser.getStatus().getId()).isEqualTo(userStatus.getId());
  }

  @Test
  @DisplayName("User 삭제 시 UserStatus도 함께 삭제된다")
  void userOrphanRemovalTest_success() {
    User user = userRepository.save(new User("test", "test@naver.com", "testPassword12!", null));
    UserStatus userStatus = new UserStatus(user, Instant.now());
    em.flush();
    em.clear();

    userRepository.delete(user);

    em.flush();
    em.clear();

    List<UserStatus> userStatuses = userStatusRepository.findAll();
    assertThat(userStatuses.size()).isEqualTo(0);
  }
}
