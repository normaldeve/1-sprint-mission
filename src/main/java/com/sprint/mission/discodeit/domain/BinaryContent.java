package com.sprint.mission.discodeit.domain;

import lombok.Data;

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
  private byte[] bytes;
  private String contentType;
  private String fileName;

  public BinaryContent(byte[] bytes, String contentType, String fileName) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.bytes = bytes;
    this.contentType = contentType;
    this.fileName = fileName;
  }
}
