package com.sprint.mission.discodeit.entity.type;

import com.sprint.mission.discodeit.entity.User;
import java.util.UUID;

public record NotificationEvent(
    User receiver,
    String title,
    String content,
    NotificationType type,
    UUID targetId
) {

}

