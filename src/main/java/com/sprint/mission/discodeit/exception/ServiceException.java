package com.sprint.mission.discodeit.exception;

import lombok.Getter;

@Getter
public class ServiceException extends DiscodeitException {

  public ServiceException(ErrorCode errorCode) {
    super(errorCode.getStatus(), errorCode.getDescription());
  }
}
