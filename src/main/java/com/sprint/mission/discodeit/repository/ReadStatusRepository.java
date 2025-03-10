package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {
    List<ReadStatus> saveAll(List<ReadStatus> readStatuses);

    List<ReadStatus> findAllByChannelId(UUID channelID);

    List<ReadStatus> findAllByUserId(UUID userID);

    void deleteAll(List<ReadStatus> readStatuses);
}
