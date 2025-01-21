package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.util.ChannelType;

import java.util.List;
import java.util.Optional;

public interface ChannelService {
    Channel create(String name, String description, ChannelType channelType);

    Optional<Channel> getChannelByName(String name);

    List<Channel> getAllChannel();

    Channel updateType(Channel channel, ChannelType channelType);

    Channel updateDescription(Channel channel, String description);

    void deleteChannel(Channel removeChannel);
}
