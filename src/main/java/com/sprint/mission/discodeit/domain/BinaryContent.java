package com.sprint.mission.discodeit.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent {
    private UUID id;
    private Instant createdAt;
    private byte[] content;
    private String contentType;
}
