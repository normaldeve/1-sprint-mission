package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;

public interface ChannelRepository {
    Channel save(Channel channel);

    Optional<Channel> getByName(String channelName);

    List<Channel> getAll();

    Channel delete(Channel channel);
}
