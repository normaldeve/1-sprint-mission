package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;
import java.util.stream.Collectors;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> channelMap;

    public JCFChannelRepository() {
        this.channelMap = new HashMap<>();
    }

    @Override
    public Channel save(Channel channel) {
        channelMap.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> getByName(String channelName) {
        return channelMap.values().stream()
                .filter(channel -> channel.getName().equals(channelName))
                .findFirst();
    }

    @Override
    public List<Channel> getAll() {
        return channelMap.values().stream()
                .collect(Collectors.toList());
    }

    @Override
    public Channel delete(Channel channel) {
        channelMap.remove(channel.getId());
        return channel;
    }
}
