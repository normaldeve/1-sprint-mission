package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

public class UpdatePublicChannel {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private UUID channelId;
        private String name;
        private String description;
        private User newUser;
    }
}
