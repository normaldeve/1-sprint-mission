package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.error.ErrorCode;
import com.sprint.mission.discodeit.exception.ServiceException;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> channelMap;

    public JCFChannelRepository() {
        this.channelMap = new HashMap<>();
    }

    @Override
    public Channel create(String name, User creator) {
        if (channelMap.values().stream()
                .anyMatch(user -> user.getName().equals(name))) {
            throw new ServiceException(ErrorCode.DUPLICATE_CHANNEL);
        }

        Channel createChannel = new Channel(name, creator);
        channelMap.put(createChannel.getId(), createChannel);
        return createChannel;
    }
}
