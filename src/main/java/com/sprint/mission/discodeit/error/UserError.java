package com.sprint.mission.discodeit.error;

public enum UserError {
    CANNOTFOUND_PHONE("해당 아이디를 찾을 수가 없습니다."),
    DUPLICATE_PHONE("이미 존재하는 아이디 입니다."),
    INVALID_PASSWORD("올바르지 않은 비밀번호 형식 입니다. 비밀번호는 8자리 이상 15자리 이하 대문자 및 특수문자 하나 이상 포함해야 합니다."),
    INVALID_PHONE("올바르지 않은 핸드폰 번호 형식 입니다."),
    EMPTY_USER("저장된 회원이 없습니다"),
    CANNOTFOUND_USER("해당하는 회원을 찾을 수 없습니다");

    private final String message;

    UserError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
