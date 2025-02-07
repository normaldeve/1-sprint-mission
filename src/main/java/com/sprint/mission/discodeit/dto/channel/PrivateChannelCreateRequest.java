package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.domain.User;
import com.sprint.mission.discodeit.util.type.ChannelFormat;
import com.sprint.mission.discodeit.util.type.ChannelType;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(UUID channelId, ChannelFormat channelFormat, List<User> users) {
}
