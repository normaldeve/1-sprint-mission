package com.sprint.mission.discodeit.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ReadStatus { // 사용자가 채널 별 마지막 메시지를 읽은 시간을 표현하는 도메인 모델
    private UUID id;
    private Instant createdAt;
    private Instant updateAt;
    private UUID userId;
    private UUID channelId;
    private LocalDateTime lastReadAt;

    public ReadStatus(UUID userId, UUID channelId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updateAt = Instant.now();
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = null;
    }

    public void updateLastReadTime() {
        this.updateAt = Instant.now();
    }
}
