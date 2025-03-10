package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository extends JpaRepository<BinaryContent, UUID> {
    List<BinaryContent> findAllIdIn(List<UUID> uuidList);
}
