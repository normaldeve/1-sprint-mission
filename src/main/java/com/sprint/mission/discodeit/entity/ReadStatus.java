package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReadStatus extends BaseEntity { // 사용자가 채널 별 마지막 메시지를 읽은 시간을 표현하는 도메인 모델
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID userId;
    private UUID channelId;
    private Instant lastReadAt;

    public void updateLastReadTime(Instant lastReadAt) {
        this.lastReadAt = lastReadAt;
    }
}
