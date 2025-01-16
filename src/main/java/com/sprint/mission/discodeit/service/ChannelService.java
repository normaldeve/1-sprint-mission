package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(Channel channel);

    Channel getChannelByName(String name);

    boolean channelExist(UUID uuid);

    List<Channel> getAllChannel();

    List<Channel> getChannelsByUserId(User user);

    Channel addUserToChannel(Channel channel, User newUser);

    Channel addManyUserToChannel(Channel channel, List<User> users);

    Channel removeUserToChannel(Channel channel, User removeUser);

    void deleteChannel(Channel removeChannel);
}
