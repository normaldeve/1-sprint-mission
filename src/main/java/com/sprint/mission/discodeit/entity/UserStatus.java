package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdateEntity;
import jakarta.persistence.Entity;
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
public class UserStatus extends BaseUpdateEntity { // 사용자 별 마지막으로 확인된 접속 시간을 표현하는 도메인 모델
    private User user;
    private Instant lastActiveAt;

    public boolean isOnline() {
        return ChronoUnit.MINUTES.between(lastActiveAt, Instant.now()) <= 5;
    }

}
