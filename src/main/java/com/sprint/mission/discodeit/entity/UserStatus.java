package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserStatus extends BaseEntity { // 사용자 별 마지막으로 확인된 접속 시간을 표현하는 도메인 모델
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Instant lastActiveAt;
    private UUID userId;
    private boolean online;

    public boolean isOnline() {
        return ChronoUnit.MINUTES.between(lastActiveAt, Instant.now()) <= 5;
    }

    public void update(Instant lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
        this.online = isOnline();
    }
}
