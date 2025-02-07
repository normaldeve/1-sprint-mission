package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.domain.UserStatus;

import java.util.UUID;

public record UserDTO(
        UUID id,
        String userName,
        String phone,
        UserStatus userStatus
) {
}
