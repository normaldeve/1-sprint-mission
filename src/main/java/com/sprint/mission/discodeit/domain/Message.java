package com.sprint.mission.discodeit.domain;

import java.util.ArrayList;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class Message implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;
  private final UUID id;
  private Instant createdAt;
  private Instant updatedAt;
  private String content;
  private UUID writerID;
  private UUID channelID;
  private List<UUID> attachmentsID;

  public Message(String content, UUID writerID, UUID channelID) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
    this.content = content;
    this.writerID = writerID;
    this.channelID = channelID;
    this.attachmentsID = new ArrayList<>();
  }

  public void update(String content, UUID newAttachment) {
    this.content = content;
    this.attachmentsID.add(newAttachment);
    this.updatedAt = Instant.now();
  }
}
