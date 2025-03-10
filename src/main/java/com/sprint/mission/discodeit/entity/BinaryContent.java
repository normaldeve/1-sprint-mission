package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString(exclude = "message")
public class BinaryContent extends BaseEntity {
  private String fileName;
  private Long size;
  private String contentType;
  private byte[] bytes;
}
