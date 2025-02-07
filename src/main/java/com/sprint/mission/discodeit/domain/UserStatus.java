package com.sprint.mission.discodeit.domain;

import com.sprint.mission.discodeit.util.type.OnlineStatusType;
import lombok.Getter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
public class UserStatus { // 사용자 별 마지막으로 확인된 접속 시간을 표현하는 도메인 모델


    private UUID id;
    private Instant createdAt;
    private Instant updateAt;
    private Instant lastActiveAt;

    public UserStatus() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updateAt = Instant.now();
        this.lastActiveAt = Instant.now();
    }

    public OnlineStatusType isOnline() {
        return ChronoUnit.MINUTES.between(lastActiveAt, Instant.now()) <= 5 ? OnlineStatusType.ACTIVE : OnlineStatusType.SLEEP;
    }
}
