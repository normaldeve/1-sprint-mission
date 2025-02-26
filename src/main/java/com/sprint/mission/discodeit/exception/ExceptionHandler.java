package com.sprint.mission.discodeit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionHandler {

  @org.springframework.web.bind.annotation.ExceptionHandler(BaseException.class)
  public ResponseEntity<ErrorResponse> handleServiceException(BaseException exception) {
    ErrorResponse response = new ErrorResponse(exception.getStatus(), exception.getMessage());
    return new ResponseEntity<>(response, exception.getStatus());
  }
}
