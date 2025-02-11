package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

public record UpdatePublicChannel(UUID channelId, String name, String description, User newUser) {
}
