package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.util.type.ChannelFormat;
import com.sprint.mission.discodeit.util.type.ChannelType;

import java.util.UUID;

public record PrivateChannelDto(
        UUID channelId,
        ChannelType channelType,
        ChannelFormat channelFormat
) {
}
