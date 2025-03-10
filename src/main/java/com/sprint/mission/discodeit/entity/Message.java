package com.sprint.mission.discodeit.entity;

import java.util.*;

import com.sprint.mission.discodeit.entity.base.BaseUpdateEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Message extends BaseUpdateEntity {
  private String content;
  private Channel channel;
  private User author;
  private List<BinaryContent> attachments;
}
