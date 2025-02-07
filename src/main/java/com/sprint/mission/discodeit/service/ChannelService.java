package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.Channel;
import com.sprint.mission.discodeit.dto.channel.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelDTO;
import com.sprint.mission.discodeit.dto.channel.CreateChannel;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelDto;
import com.sprint.mission.discodeit.util.type.ChannelType;

import java.util.List;
import java.util.Optional;

public interface ChannelService {
    ChannelDTO.PublicChannel createPublicChannel(CreateChannel.PublicRequest request);

    ChannelDTO.PrivateChannel createPrivateChannel(CreateChannel.PrivateRequest request);

    Optional<Channel> getChannelByName(String name);

    List<Channel> getAllChannel();

    Channel updateDescription(Channel channel, String description);

    void delete(Channel removeChannel);
}
