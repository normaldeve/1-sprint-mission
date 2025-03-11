package com.sprint.mission.discodeit.entity;

import java.util.*;

import com.sprint.mission.discodeit.entity.base.BaseUpdateEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "messages")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"author", "channel"})
public class Message extends BaseUpdateEntity {
  private String content;

  @ManyToOne
  @NotNull
  private Channel channel;

  @ManyToOne
  private User author;

  @ManyToMany
  @JoinTable(
          name = "message_attachments", // 중간 테이블 이름
          joinColumns = @JoinColumn(name = "message_id"), // `Message` 테이블 외래 키
          inverseJoinColumns = @JoinColumn(name = "attachment_id") // `BinaryContent` 테이블 외래 키
  )
  @Builder.Default
  private List<BinaryContent> attachments;
}
