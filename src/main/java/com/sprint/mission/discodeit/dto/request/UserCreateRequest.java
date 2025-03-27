package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserCreateRequest(
    @NotBlank(message = "회원 이름 입력은 필수입니다.")
    String username,
    @Email
    @NotBlank(message = "이메일 입력은 필수입니다")
    String email,
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[\\W_])(?=.*[a-zA-Z\\d]).{8,15}$", message = "8자리 이상 15자리 이하 대문자 및 특수문자 하나 이상 포함해야 합니다")
    @NotBlank(message = "비밀번호 입력은 필수입니다")
    String password
) {

}
