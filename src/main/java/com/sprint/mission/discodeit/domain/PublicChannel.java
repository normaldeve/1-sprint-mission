package com.sprint.mission.discodeit.domain;

import com.sprint.mission.discodeit.util.type.ChannelFormat;
import com.sprint.mission.discodeit.util.type.ChannelType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class PublicChannel extends Channel {
    private String name;
    private String description;
    private ChannelFormat channelFormat;
    private ChannelType channelType;

    public PublicChannel(String name, String description, ChannelFormat channelFormat) {
        this.name = name;
        this.description = description;
        this.channelFormat = channelFormat;
        this.channelType = ChannelType.PUBLIC;
    }
}
