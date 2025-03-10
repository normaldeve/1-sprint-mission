package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdateEntity;
import jakarta.persistence.Entity;

import lombok.*;

import java.time.Instant;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReadStatus extends BaseUpdateEntity { // 사용자가 채널 별 마지막 메시지를 읽은 시간을 표현하는 도메인 모델
    private User user;
    private Channel channel;
    private Instant lastReadAt;
}
