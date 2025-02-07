package com.sprint.mission.discodeit.domain;

import com.sprint.mission.discodeit.util.type.ChannelFormat;
import com.sprint.mission.discodeit.util.type.ChannelType;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class PrivateChannel extends Channel {
    private List<User> joinMembers;
    private ChannelFormat channelFormat;
    private ChannelType channelType;

    public PrivateChannel(List<User> joinMembers, ChannelFormat channelFormat) {
        this.joinMembers = joinMembers;
        this.channelFormat = channelFormat;
        this.channelType = ChannelType.PRIVATE;
    }
}
