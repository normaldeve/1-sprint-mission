package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse {

  private final int status;

  private final String error;

  private final String message;

  private final Instant timestamp;

  public ErrorResponse(HttpStatus status, String message) {
    this.status = status.value();
    this.error = status.getReasonPhrase();
    this.message = message;
    this.timestamp = Instant.now();
  }
}
