package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.dto.user.UserDTO;
import com.sprint.mission.discodeit.util.type.ChannelType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChannelDTO {
  private UUID id;

  @NotNull
  private ChannelType type;

  private String name;

  private String description;

  private List<UserDTO> participants;

  private Instant lastMessageAt;
}
