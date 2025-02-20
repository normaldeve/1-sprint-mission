package com.sprint.mission.discodeit.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Data
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    @NotNull(message = "회원 이름은 필수 입력 값입니다")
    private String name;

    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식에 맞춰 입력해주세요")
    private String phone;

    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[\\W_])(?=.*[a-zA-Z\\d]).{8,15}$", message = "8자리 이상 15자리 이하 대문자 및 특수문자 하나 이상 포함해야 합니다")
    private String password;
    private UUID profileImageId;
    private UUID userStatusId;

    public User(String name, String phone, String password, UUID profileImageId, UUID userStatusId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.profileImageId = profileImageId;
        this.userStatusId = userStatusId;
    }

    public void update(String password) {
        this.password = password;
        this.updatedAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) { // User 객체는 UUID, name, phone 3개의 필드가 동일하면 같은 유저라고 판단한다.
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(name, user.name) && Objects.equals(phone, user.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, phone);
    }
}
