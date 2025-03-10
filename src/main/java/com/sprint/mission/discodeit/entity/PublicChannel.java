package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Entity;
import lombok.*;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
public class PublicChannel extends Channel {

  private String name;
  private String description;

  public void update(String name, String description, UUID newUserID) {
    this.name = name;
    this.description = description;
  }
}
