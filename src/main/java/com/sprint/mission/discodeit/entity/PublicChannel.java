package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
public class PublicChannel extends Channel {

  private String name;
  private String description;

  public PublicChannel(String name, String description) {
    super();
    this.name = name;
    this.description = description;
  }

  public void update(String name, String description, UUID newUserID) {
    this.name = name;
    this.description = description;
  }
}
