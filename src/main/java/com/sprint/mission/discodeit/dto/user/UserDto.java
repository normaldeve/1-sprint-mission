package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record UserDto(
        UUID id,
        String name,
        String phone
) {
}
