package com.sprint.mission.discodeit.domain;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus {
    private UUID id;
    private Instant createdAt;
    private Instant updateAt;
    private UUID userId;
    private UUID channelId;

    public void updateLastReadTime() {
        this.updateAt = Instant.now();
    }
}
