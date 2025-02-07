package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.domain.User;
import com.sprint.mission.discodeit.util.type.ChannelFormat;
import com.sprint.mission.discodeit.util.type.ChannelType;

import java.util.List;

public record PublicChannelCreateRequest(
        String name, String description, ChannelFormat channelFormat, List<User> users
) {
}
