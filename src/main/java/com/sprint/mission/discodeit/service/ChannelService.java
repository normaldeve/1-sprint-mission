package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.UUID;

public interface ChannelService {
    void createChannel(Channel channel);

    Channel getChannelById(UUID id);

    Channel updateChannelById(UUID id, User newUser);

    void deleteChannel(Channel channel);
}
