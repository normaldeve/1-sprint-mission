package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

public interface ChannelService {
    void createChannel();

    Channel getChannelById();

    Channel updateChannel();

    void deleteChannel();
}
