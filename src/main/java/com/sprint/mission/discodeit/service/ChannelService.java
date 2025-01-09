package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(List<User> members, String name, User creator);

    Channel getChannelById(UUID id);

    Channel updateChannelById(UUID id, User newUser);

    List<Channel> getAllChannel();

    void deleteChannel(Channel channel);
}
