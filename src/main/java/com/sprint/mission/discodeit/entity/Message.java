package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private UUID id;
    private Long createdAt;
    private Long updatedAt;
    private String content;
    private User fromUser;
    private User toUser;

    public Message(String content, User fromUser ,User toUser) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = null;
        this.content = content;
        this.fromUser = fromUser;
        this.toUser = toUser;
    }

    public String getContent() {
        return content;
    }

    public User getToUser() {
        return toUser;
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

    public User getFromUser() {
        return fromUser;
    }

    public void update(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }
}
