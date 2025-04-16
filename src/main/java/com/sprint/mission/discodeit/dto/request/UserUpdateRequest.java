package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record UserUpdateRequest(
    String newUsername,
    @Email
    String newEmail,
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[\\W_])(?=.*[a-zA-Z\\d]).{8,15}$", message = "8자리 이상 15자리 이하 대문자 및 특수문자 하나 이상 포함해야 합니다")
    String newPassword
) {

}
