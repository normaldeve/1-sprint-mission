package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.domain.Channel;
import com.sprint.mission.discodeit.domain.PublicChannel;
import com.sprint.mission.discodeit.dto.channel.CreateChannel;
import com.sprint.mission.discodeit.dto.channel.ChannelDTO;
import com.sprint.mission.discodeit.dto.channel.UpdatePublicChannel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

  Channel createPublicChannel(CreateChannel.PublicRequest request);

  Channel createPrivateChannel(CreateChannel.PrivateRequest request);

  ChannelDTO.PublicChannelDTO findPublicChannel(UUID channelId);

  ChannelDTO.PrivateChannelDTO findPrivateChannel(UUID channelId);

  List<Channel> findAllPrivate(UUID userId);

  List<Channel> findAll();

  PublicChannel update(UUID channelId, UpdatePublicChannel request);

  Channel deletePrivate(UUID channelId);

  Channel deletePublic(UUID channelId);
}
