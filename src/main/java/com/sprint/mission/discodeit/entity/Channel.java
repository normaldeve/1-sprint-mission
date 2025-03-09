package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class Channel extends BaseEntity implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;
  private final UUID id;

  public Channel() {
    this.id = UUID.randomUUID();
  }
}
