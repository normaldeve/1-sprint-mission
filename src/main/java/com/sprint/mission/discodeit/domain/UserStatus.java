package com.sprint.mission.discodeit.domain;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus {
    private UUID id;
    private Instant createdAt;
    private Instant updateAt;
    private Instant lastLoginAt;

    public boolean isUserOnline() {
        return lastLoginAt.isAfter(Instant.now().minusSeconds(5 * 60));
    }
}
