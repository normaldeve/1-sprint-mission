package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.domain.Channel;
import com.sprint.mission.discodeit.domain.User;
import com.sprint.mission.discodeit.util.type.ChannelFormat;
import com.sprint.mission.discodeit.util.type.ChannelType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ChannelDTO {
    @Getter
    @Setter
    @Builder
    public static class PrivateChannel {
        private UUID id;
        private Instant createdAt;
        private Instant updatedAt;
        private ChannelFormat channelFormat;
        private ChannelType channelType;
        private List<User> joinUsers;

        public static PrivateChannel fromDomain(Channel channel, List<User> joinUsers) {
            return PrivateChannel.builder()
                    .id(channel.getId())
                    .createdAt(channel.getCreatedAt())
                    .updatedAt(channel.getUpdatedAt())
                    .channelFormat(channel.getChannelFormat())
                    .channelType(ChannelType.PRIVATE)
                    .joinUsers(joinUsers)
                    .build();
        }
    }

    @Getter
    @Setter
    @Builder
    public static class PublicChannel {
        private UUID id;
        private Instant createdAt;
        private Instant updatedAt;
        private String name;
        private String description;
        private ChannelFormat channelFormat;
        private ChannelType channelType;

        public static PublicChannel fromDomain(Channel channel) {
            return PublicChannel.builder()
                    .id(channel.getId())
                    .createdAt(channel.getCreatedAt())
                    .updatedAt(channel.getUpdatedAt())
                    .name(channel.getName())
                    .description(channel.getDescription())
                    .channelFormat(channel.getChannelFormat())
                    .channelType(ChannelType.PUBLIC)
                    .build();
        }
    }
}
