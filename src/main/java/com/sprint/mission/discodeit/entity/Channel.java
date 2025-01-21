package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class Channel implements Serializable {
    private UUID id;
    private Long createdAt;
    private Long updatedAt;
    private List<User> members;
    private String name;
    private User creator;

    public Channel(String name, User creator) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = null;
        this.members = new ArrayList<>();
        this.name = name;
        this.creator = creator;
    }

    public void addUser(User user) {
        this.members.add(user);
        this.updatedAt = System.currentTimeMillis();
    }

    public void addManyUser(List<User> users) {
        this.members.addAll(users);
        this.updatedAt = System.currentTimeMillis();
    }

    public void removeUser(User user) {
        members.remove(user);
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String createdAtFormatted = createdAt != null
                ? LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAt), ZoneId.systemDefault()).format(formatter)
                : "N/A";
        String updatedAtFormatted = updatedAt != null
                ? LocalDateTime.ofInstant(Instant.ofEpochMilli(updatedAt), ZoneId.systemDefault()).format(formatter)
                : "N/A";

        String creatorName = creator != null ? creator.getName() : "Unknown";

        String membersNames = (members != null && !members.isEmpty())
                ? members.stream().map(User::getName).collect(Collectors.joining(", "))
                : "No members";

        return "Channel{" +
                "id=" + id +
                ", createdAt=" + createdAtFormatted +
                ", updatedAt=" + updatedAtFormatted +
                ", members=" + membersNames +
                ", name='" + name + '\'' +
                ", creator='" + creatorName + '\'' +
                '}';
    }

}
