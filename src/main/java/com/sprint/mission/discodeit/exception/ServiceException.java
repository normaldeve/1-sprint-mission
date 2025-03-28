package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public class ServiceException extends DiscodeitException {

  private final ErrorCode errorCode;

  public ServiceException(ErrorCode errorCode) {
    super(errorCode.getStatus(), errorCode.getDescription());
    this.errorCode = errorCode;
  }
}
