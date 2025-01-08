package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User {
    private UUID id;
    private Long createdAt;
    private Long updatedAt;
    private String name;
    private String password;

    public User(String name, String password) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = null;
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void update(String password) {
        this.password = password;
        this.updatedAt = System.currentTimeMillis();
    }
}
