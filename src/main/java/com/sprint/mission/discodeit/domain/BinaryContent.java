package com.sprint.mission.discodeit.domain;

import com.sprint.mission.discodeit.util.type.BinaryContentType;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent {
    private UUID id;
    private Instant createdAt;
    private byte[] content;
    private BinaryContentType contentType;

    public BinaryContent(byte[] content, BinaryContentType contentType) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.content = content;
        this.contentType = contentType;
    }

    public void updateContent(byte[] newContent) {
        this.content = newContent;
    }
}
