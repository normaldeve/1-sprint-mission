package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.PublicChannel;
import com.sprint.mission.discodeit.dto.channel.CreateChannel;
import com.sprint.mission.discodeit.dto.channel.UpdatePublicChannel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

  Channel createPublicChannel(CreateChannel.PublicRequest request);

  Channel createPrivateChannel(CreateChannel.PrivateRequest request);

  List<Channel> findAllPrivate(UUID userId);

  List<Channel> findAllPublic();

  List<Channel> findAll();

  PublicChannel update(UUID channelId, UpdatePublicChannel request);

  Channel delete(UUID channelId);

}