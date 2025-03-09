package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

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
  private List<BinaryContent> binaryContents = new HashSet<>();

  public void update(String content, UUID newAttachment) {
    this.content = content;
    this.attachmentsID.add(newAttachment);
  }
}
