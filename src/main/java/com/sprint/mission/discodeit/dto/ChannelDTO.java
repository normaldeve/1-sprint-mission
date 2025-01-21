package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChannelDTO {
    private UUID id;
    private Long createdAt;
    private Long updatedAt;
    private List<User> members;
    private String channelName;
    private String creatorId;

    public static ChannelDTO fromEntity(Channel channel) {
        return ChannelDTO.builder()
                .id(channel.getId())
                .createdAt(channel.getCreatedAt())
                .updatedAt(channel.getUpdatedAt())
                .members(channel.getMembers())
                .channelName(channel.getName())
                .creatorId(channel.getCreator().getPhone())
                .build();
    }
}
