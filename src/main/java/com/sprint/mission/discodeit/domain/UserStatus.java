package com.sprint.mission.discodeit.domain;

import lombok.Getter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
public class UserStatus {
    private UUID id;
    private Instant createdAt;
    private Instant updateAt;
    private UUID userId;
    private Instant lastActiveAt;

    public boolean isOnline() {
        return ChronoUnit.MINUTES.between(lastActiveAt, Instant.now()) <= 5;
    }
}
