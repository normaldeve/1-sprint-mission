package com.sprint.mission.discodeit.domain;

import com.sprint.mission.discodeit.util.type.ChannelFormat;
import com.sprint.mission.discodeit.util.type.ChannelType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public abstract class Channel implements Serializable {
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private List<User> joinMembers;

    public Channel(List<User> joinMembers) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.joinMembers = joinMembers;
    }
}
