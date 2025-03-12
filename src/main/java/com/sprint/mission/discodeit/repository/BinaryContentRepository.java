package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BinaryContentRepository extends JpaRepository<BinaryContent, UUID> {
    @Query("SELECT b FROM BinaryContent b WHERE b.id IN :ids")
    List<BinaryContent> findAllIdIn(@Param("ids") List<UUID> ids);

}
