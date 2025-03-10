package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "binary_contents")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
public class BinaryContent extends BaseEntity {
  @Column(nullable = false)
  private String fileName;
  @Column(nullable = false)
  private Long size;
  @Column(nullable = false)
  private String contentType;
  @Column(nullable = false)
  private byte[] bytes;
}
