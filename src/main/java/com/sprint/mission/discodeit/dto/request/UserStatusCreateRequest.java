package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.UUID;

public record UserStatusCreateRequest(
    @NotBlank(message = "회원 아이디 입력은 필수입니다")
    UUID userId,
    Instant lastActiveAt
) {

}
