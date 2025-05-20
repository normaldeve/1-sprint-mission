package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.security.role.Role;
import java.util.UUID;

public record UserRoleUpdateRequest(
    UUID userId,
    Role newRole
) {

}
