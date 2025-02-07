package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.Channel;
import com.sprint.mission.discodeit.dto.channel.CreateChannel;
import com.sprint.mission.discodeit.dto.channel.FindChannel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    Channel createPublicChannel(CreateChannel.PublicRequest request);

    Channel createPrivateChannel(CreateChannel.PrivateRequest request);

    FindChannel.PublicResponse findPublicChannel(UUID channelId);

    FindChannel.PrivateResponse findPrivateChannel(UUID channelId);

    List<Channel> findAllPublic();

    List<Channel> findAllPrivate();

    Channel updateDescription(Channel channel, String description);

    void delete(Channel removeChannel);
}
