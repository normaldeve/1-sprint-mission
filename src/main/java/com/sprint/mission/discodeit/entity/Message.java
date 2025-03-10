package com.sprint.mission.discodeit.entity;

import java.util.*;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Message extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  private String content;
  private UUID writerID;
  private UUID channelID;
  @OneToOne(mappedBy = "message",
  cascade = {CascadeType.ALL},
  fetch = FetchType.LAZY,
  orphanRemoval = true)
  @Builder.Default
  @BatchSize(size = 20)
  private Set<BinaryContent> binaryContents = new HashSet<>();

}
