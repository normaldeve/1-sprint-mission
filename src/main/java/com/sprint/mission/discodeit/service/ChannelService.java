package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelDTO;
import com.sprint.mission.discodeit.dto.channel.CreateChannel;
import com.sprint.mission.discodeit.dto.channel.UpdatePublicChannel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

  ChannelDTO createPublicChannel(CreateChannel.PublicRequest request);

  ChannelDTO createPrivateChannel(CreateChannel.PrivateRequest request);

  ChannelDTO find(UUID channelId);

  List<ChannelDTO> findAllByUserId(UUID userId);

  ChannelDTO update(UUID channelId, UpdatePublicChannel request);

  void delete(UUID channelId);

}