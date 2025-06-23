package com.sprint.mission.discodeit.entity.type;

import java.util.UUID;

public record NotificationEvent(
    UUID receiverId,
    String title,
    String content,
    NotificationType type,
    UUID targetId
) {

}

