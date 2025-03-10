package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdateEntity;
import com.sprint.mission.discodeit.util.type.ChannelType;
import jakarta.persistence.Entity;
import lombok.*;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
public class Channel extends BaseUpdateEntity {

  private ChannelType type;
  private String name;
  private String description;

}
