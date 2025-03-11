package com.sprint.mission.discodeit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  EMPTY_CONTENT(HttpStatus.BAD_REQUEST, "내용을 입력해주세요"),

  CANNOT_FOUND_MESSAGE(HttpStatus.NOT_FOUND, "보낸 메시지가 없습니다"),
  CANNOT_FOUND_CHANNEL(HttpStatus.NOT_FOUND, "해당 채널을 찾을 수가 없습니다."),
  CANNOT_FOUND_USER(HttpStatus.NOT_FOUND, "해당하는 회원을 찾을 수 없습니다"),
  CANNOT_FOUND_PROFILE(HttpStatus.NOT_FOUND, "해당하는 프로필 데이터를 찾을 수 없습니다"),
  CANNOT_FOUND_USERSTATUS(HttpStatus.NOT_FOUND, "해당하는 사용자의 상태 정보를 찾을 수 없습니다"),
  CANNOT_FOUND_READSTATUS(HttpStatus.NOT_FOUND, "해당하는 Read Status 정보를 찾을 수 없습니다"),
  CANNOT_FOUND_ATTACHMENT(HttpStatus.NOT_FOUND, "해당하는 첨부자료를 찾을 수 없습니다"),

  DUPLICATE_NAME(HttpStatus.CONFLICT, "이미 등록된 사용자 이름입니다."),
  DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 등록된 이메일 입니다."),
  DUPLICATE_CHANNEL(HttpStatus.CONFLICT, "이미 존재하는 채널 이름 입니다."),

  ALREADY_EXIST_READSTATUS(HttpStatus.CONFLICT, "이미 저장된 Read Status 입니다."),
  ALREADY_EXIST_USERSTAUTS(HttpStatus.CONFLICT, "이미 저장된 User Status 입니다."),

  INVALID_WRITER(HttpStatus.BAD_REQUEST, "작성자가 올바르지 않습니다"),
  INVALID_PROFILE(HttpStatus.BAD_REQUEST, "기존 프로필과 동일한 프로필입니다"),

  MESSAGE_EDIT_NOT_ALLOWED(HttpStatus.FORBIDDEN, "메시지 수정은 작성자 본인만 가능합니다"),
  PASSWORD_EDIT_NOT_ALLOWED(HttpStatus.FORBIDDEN, "이전 비밀번호와 일치하지 않습니다"),

  PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다"),
  USERNAME_MISMATCH(HttpStatus.UNAUTHORIZED, "이름이 일치하지 않습니다"),
  CHANNEL_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "채널 타입이 일치하지 않습니다"),

  SAME_AS_OLD_PASSWORD(HttpStatus.BAD_REQUEST, "이전 비밀번호와 동일한 비밀번호입니다"),
  CANNOT_MODIFY_PRIVATE_CHANNEL(HttpStatus.BAD_REQUEST, "Private 채널은 수정할 수 없습니다");
  private final HttpStatus status;
  private final String description;
}
