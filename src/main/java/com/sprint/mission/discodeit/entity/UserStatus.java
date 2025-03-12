package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdateEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "user_statuses")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "user")
public class UserStatus extends BaseUpdateEntity { // 사용자 별 마지막으로 확인된 접속 시간을 표현하는 도메인 모델
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Instant lastActiveAt;

    public void update() {
        this.lastActiveAt = Instant.now();
    }
}
