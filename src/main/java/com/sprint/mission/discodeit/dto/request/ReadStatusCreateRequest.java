package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record ReadStatusCreateRequest(
    @NotNull(message = "회원아이디 입력은 필수입니다")
    UUID userId,
    @NotNull(message = "채널 아이디 입력은 필수입니다")
    UUID channelId,
    Instant lastReadAt
) {

}
