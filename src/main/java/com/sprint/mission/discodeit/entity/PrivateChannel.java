package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@ToString
public class PrivateChannel extends Channel { //

  private List<UUID> joinMembers;

  public PrivateChannel(List<UUID> joinMember) {
    super();
    this.joinMembers = joinMember;
  }
}
