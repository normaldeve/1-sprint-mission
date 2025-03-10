package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Entity;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
public class PrivateChannel extends Channel { //

  private List<UUID> joinMembers;
}
