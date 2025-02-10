package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.domain.Channel;
import com.sprint.mission.discodeit.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateMessage {
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        private List<UUID> attachmentsID;
        private String content;
        private User writer;
        private Channel channel;
    }
}
