package com.sprint.mission.discodeit.entity;

import java.util.List;
import java.util.UUID;

public class Channel {
    private UUID id;
    private Long createdAt;
    private Long updatedAt;
    private List<User> members;
    private String name;
    private User creator;

    public Channel(List<User> members, String name, User creator) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = null;
        this.members = members;
        this.name = name;
        this.creator = creator;
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

    public User getCreator() {
        return creator;
    }

    public void update(User user) {
        members.add(user);
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", members=" + members +
                ", name='" + name + '\'' +
                ", creator=" + creator +
                '}';
    }
}
