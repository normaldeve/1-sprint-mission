package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {
    Optional<UserStatus> findByUserId(UUID userId);

    @Query("SELECT u FROM UserStatus u WHERE u.lastActiveAt >= CURRENT_TIMESTAMP - 5")
    List<UserStatus> findByIsOnlineTrue();

    void deleteByUserId(UUID userId);
}
