package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdateEntity;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ServiceException;
import jakarta.persistence.Entity;
import lombok.*;

import java.util.UUID;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User extends BaseUpdateEntity {
    private String username;
    private String email;
    private String password;
    private BinaryContent profile;
    private UserStatus status;
}