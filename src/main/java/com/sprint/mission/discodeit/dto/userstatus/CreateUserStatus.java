package com.sprint.mission.discodeit.dto.userstatus;

import com.sprint.mission.discodeit.util.type.BinaryContentType;

import java.util.UUID;

public class CreateUserStatus {
    public static class Request {
        private UUID userId;

        private byte[] content;

        private BinaryContentType binaryContentType;
    }
}
