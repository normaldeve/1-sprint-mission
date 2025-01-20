package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.error.ChannelError;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.sprint.mission.discodeit.error.ChannelError.DUPLICATE_NAME;
import static com.sprint.mission.discodeit.error.UserError.DUPLICATE_PHONE;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> channelMap;

    public JCFChannelRepository() {
        this.channelMap = new HashMap<>();
    }

    @Override
    public Channel create(String name, User creator) {
        if (channelMap.values().stream()
                .anyMatch(user -> user.getName().equals(name))) {
            throw new IllegalArgumentException(DUPLICATE_NAME.getMessage());
        }

        Channel createChannel = new Channel(name, creator);
        channelMap.put(createChannel.getId(), createChannel);
        return createChannel;
    }
}
