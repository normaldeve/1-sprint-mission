package com.sprint.mission.discodeit.domain;

import com.sprint.mission.discodeit.util.type.ChannelFormat;
import com.sprint.mission.discodeit.util.type.ChannelType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class Channel implements Serializable {
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private String name;
    private String description;
    private ChannelFormat channelFormat;
    private ChannelType channelType;

    public Channel(String name, String description, ChannelFormat channelFormat, ChannelType channelType) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.name = name;
        this.description = description;
        this.channelFormat = channelFormat;
        this.channelType = channelType;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Channel channel = (Channel) o;
        return Objects.equals(name, channel.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    public void changeFormat(ChannelFormat channelFormat) {
        this.channelFormat = channelFormat;
        this.updatedAt = Instant.now();
    }

    public void changeDescription(String description) {
        this.description = description;
        this.updatedAt = Instant.now();
    }

    @Override
    public String toString() {

        return "Channel{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", description=" + description +
                ", name='" + name + '\'' +
                ", type='" + channelType + '\'' +
                '}';
    }

}
