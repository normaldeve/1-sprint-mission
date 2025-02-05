package com.sprint.mission.discodeit.domain;

import com.sprint.mission.discodeit.util.OnlineStatus;
import lombok.Getter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
public class UserStatus { // 사용자 별 마지막으로 확인된 접속 시간을 표현하는 도메인 모델


    private UUID id;
    private Instant createdAt;
    private Instant updateAt;
    private UUID userId;
    private Instant lastActiveAt;

    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updateAt = Instant.now();
        this.userId = userId;
        this.lastActiveAt = Instant.now();
    }

    public OnlineStatus isOnline() {
        return ChronoUnit.MINUTES.between(lastActiveAt, Instant.now()) <= 5 ? OnlineStatus.ACTIVE : OnlineStatus.SLEEP;
    }
}
