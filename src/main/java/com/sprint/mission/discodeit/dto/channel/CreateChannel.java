package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.util.type.ChannelFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

public class CreateChannel {

  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class PrivateRequest {

    private List<UUID> joinUser;
  }

  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class PublicRequest {

    private String name;
    private String description;
  }
}
