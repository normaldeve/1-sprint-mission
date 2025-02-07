package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.domain.UserStatus;
import com.sprint.mission.discodeit.util.type.ProfileUse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.UUID;

public class CreateUser {
    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request {
        @NonNull
        private String name;
        @NonNull
        private String phone;
        @NonNull
        private String password;
        private UUID profileId;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Response {
        private UUID userId;
        private ProfileUse profileUse;
    }
}
