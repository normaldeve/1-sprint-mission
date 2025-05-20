package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByUsername(String username);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);

  @Query("SELECT u FROM User u "
      + "LEFT JOIN FETCH u.profile "
      + "JOIN FETCH u.status")
  List<User> findAllWithProfileAndStatus();

  @Query("""
    SELECT u
    FROM User u
    LEFT JOIN FETCH u.profile
    LEFT JOIN FETCH u.status
    WHERE u.id = :id
""")
  Optional<User> findByIdWithDetails(@Param("id") UUID id);

}
