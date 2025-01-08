package com.sprint.mission.discodeit.entity;

import java.util.List;
import java.util.UUID;

public class Channel {
    private UUID id;
    private Long createdAt;
    private Long updatedAt;
    private List<User> members;
    private String name;

    public Channel(List<User> members, String name) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = null;
        this.members = members;
        this.name = name;
    }

    public List<User> getMembers() {
        return members;
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void update(String name, List<User> members) {
        this.name = name;
        this.members = members;
        this.updatedAt = System.currentTimeMillis();
    }
}
