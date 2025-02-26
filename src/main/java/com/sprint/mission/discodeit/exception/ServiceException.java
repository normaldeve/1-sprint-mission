package com.sprint.mission.discodeit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ServiceException extends BaseException {

  public ServiceException(ErrorCode errorCode) {
    super(errorCode.getStatus(), errorCode.getDescription());
  }
}

