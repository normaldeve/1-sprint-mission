package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {
    UserStatus save(UserStatus userStatus);

    Optional<UserStatus> findByUserId(UUID userID);

    Optional<UserStatus> findById(UUID id);

    List<UserStatus> findByIsOnlineTrue();

    List<UserStatus> findAll();

    void deleteById(UUID id);

    void deleteByUserId(UUID userID);
}
