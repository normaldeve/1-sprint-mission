package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdateEntity;
import com.sprint.mission.discodeit.util.type.ChannelType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "channels")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
public class Channel extends BaseUpdateEntity {

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ChannelType type;
  private String name;
  private String description;

  public void update(String name, String description) {
    this.name = name;
    this.description = description;
  }
}
