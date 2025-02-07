package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.Channel;
import com.sprint.mission.discodeit.dto.channel.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelDto;
import com.sprint.mission.discodeit.util.type.ChannelType;

import java.util.List;
import java.util.Optional;

public interface ChannelService {
    Channel createPublicChannel(ChannelCreateRequest request);

    PrivateChannelDto createPrivateChannel(ChannelCreateRequest request);

    Optional<Channel> getChannelByName(String name);

    List<Channel> getAllChannel();

    Channel updateType(Channel channel, ChannelType channelType);

    Channel updateDescription(Channel channel, String description);

    void delete(Channel removeChannel);
}
