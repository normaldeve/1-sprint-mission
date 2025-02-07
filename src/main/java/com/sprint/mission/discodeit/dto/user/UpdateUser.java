package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.util.type.ProfileUse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.UUID;

public class UpdateUser {
    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request {
        @NonNull
        private UUID userId;

        @NonNull
        private UUID profileId;

        private byte[] content;
    }


    @Getter
    @Setter
    @AllArgsConstructor
    public static class Response {
        private UUID userId;
        private ProfileUse profileUse;
    }
}
