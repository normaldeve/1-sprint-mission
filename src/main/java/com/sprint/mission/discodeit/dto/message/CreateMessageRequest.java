package com.sprint.mission.discodeit.dto.message;


import java.util.List;
import java.util.UUID;

public record CreateMessageRequest(UUID userId, String content,
                                   UUID channelId) {

}
