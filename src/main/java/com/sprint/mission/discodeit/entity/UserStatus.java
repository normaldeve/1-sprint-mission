package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdateEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserStatus extends BaseUpdateEntity { // 사용자 별 마지막으로 확인된 접속 시간을 표현하는 도메인 모델
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Instant lastActiveAt;

    public void update() {
        this.lastActiveAt = Instant.now();
    }
}
