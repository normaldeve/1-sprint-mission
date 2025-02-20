package com.sprint.mission.discodeit.domain;

import com.sprint.mission.discodeit.util.type.BinaryContentType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
public class BinaryContent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private Instant createdAt;
    private byte[] content;
    private String filePath;
    private BinaryContentType contentType;
    private String originalFileName;
    private String storedFileName;

    public BinaryContent(byte[] content, String filePath, BinaryContentType contentType, String originalFileName, String storedFileName) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.content = content;
        this.contentType = contentType;
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.filePath = filePath;
    }

    public void updateContent(byte[] newContent) {
        this.content = newContent;
    }
}
