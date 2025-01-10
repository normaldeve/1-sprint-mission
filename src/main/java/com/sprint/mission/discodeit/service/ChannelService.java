package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;

public interface ChannelService {
    Channel createChannel(List<User> members, String name, User creator);

    Channel getChannelByName(String name);

    List<Channel> getAllChannel();

    List<Channel> getChannelsByUserId(User user);

    Channel addUserToChannel(String name, User newUser);

    Channel removeUserFromChannel(String name, User removeUser);

    boolean deleteChannel(String name);

//    void removeUserFromAllChannels(User user);
}
