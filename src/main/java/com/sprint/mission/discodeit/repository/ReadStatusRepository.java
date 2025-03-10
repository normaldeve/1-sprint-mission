package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {
    ReadStatus save(ReadStatus readStatus);

    List<ReadStatus> saveAll(List<ReadStatus> readStatuses);

    Optional<ReadStatus> findById(UUID id);

    List<ReadStatus> findAllByChannelId(UUID channelID);

    List<ReadStatus> findAllByUserId(UUID userID);

    void delete(ReadStatus readStatus);

    void deleteAll(List<ReadStatus> readStatuses);
}
