package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record UpdateUser(UUID userId, String password, byte[] profile) {
}
