package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsById(UUID userId);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String name);

    List<User> findAllByIdIn(List<UUID> userIds);
}
