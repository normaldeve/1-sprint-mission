package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString(exclude = "message")
public class BinaryContent {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  @CreatedDate
  @Column(name = "createdAt", updatable = false)
  private Instant createdAt;

  private byte[] bytes;
  private String contentType;
  private String fileName;

  @ManyToOne
  private Message message;
}
