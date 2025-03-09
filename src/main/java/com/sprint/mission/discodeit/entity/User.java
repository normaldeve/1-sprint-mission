package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ServiceException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String email;
    private String password;
    private UUID profileImageId;
    private UUID userStatusId;
    private boolean online;

    public void updatePassword(String oldPassword, String newPassword) {
        if (!this.password.equals(oldPassword) || oldPassword.equals(newPassword)) {
            throw new ServiceException(ErrorCode.PASSWORD_MISMATCH);
        }
        this.password = newPassword;
    }

    public void updateProfile(UUID profileImageId, UUID newProfileImageId) {
        if (!this.profileImageId.equals(profileImageId) || profileImageId.equals(newProfileImageId)) {
            throw new ServiceException(ErrorCode.INVALID_PROFILE);
        }
        this.profileImageId = newProfileImageId;
    }

    @Override
    public boolean equals(Object o) { // User 객체는 UUID, name, phone 3개의 필드가 동일하면 같은 유저라고 판단한다.
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(name, user.name) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email);
    }
}