package com.sprint.mission.discodeit.error;

public enum ChannelError {
    DUPLICATE_NAME("이미 존재하는 채널입니다"),
    CANNOTFOUND_NAME("해당 채널 이름이 존재하지 않습니다"),
    EMPTY_CHANNEL("등록된 채널이 없습니다"),
    EMPTY_USER("해당 아이디로 등록된 회원이 없습니다"),
    ;

    private String message;

    ChannelError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
