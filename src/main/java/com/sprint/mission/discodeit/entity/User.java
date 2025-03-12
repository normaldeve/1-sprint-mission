package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdateEntity;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ServiceException;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.*;

@Entity
@Table(name = "users")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "status")
public class User extends BaseUpdateEntity {
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "profile_id")
    private BinaryContent profile;

    @OneToOne(mappedBy = "user", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private UserStatus status;

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public void updatePassword(String oldPassword, String newPassword) {
        if (newPassword.equals(this.password)) {
            throw new ServiceException(ErrorCode.SAME_AS_OLD_PASSWORD);
        }

        if (!oldPassword.equals(this.password)) {
            throw new ServiceException(ErrorCode.PASSWORD_MISMATCH);
        }

        if (newPassword.equals(oldPassword)) {
            throw new ServiceException(ErrorCode.SAME_AS_OLD_PASSWORD);
        }

        this.password = newPassword;
    }

    public void updateProfile(BinaryContent profile) {
        this.profile = profile;
    }
}