package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.Map;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DiscodeitException extends RuntimeException {

  private final Instant timestamp;

  private final HttpStatus status;

  private final ErrorCode errorCode;

  private final Map<String, Object> details;

  public DiscodeitException(HttpStatus status, String message, ErrorCode errorCode, Map<String, Object> details) {
    super(message);
    this.status = status;
    this.timestamp = Instant.now();
    this.errorCode = errorCode;
    this.details = details;
  }
}
