package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    User save(User user);

    boolean userExistById(UUID userID);

    Optional<User> findByEmail(String email);

    Optional<User> findByName(String name);

    Optional<User> findById(UUID userId);

    List<User> findAll();

    List<User> findAllById(List<UUID> userIds);

    void delete(UUID id);
}
