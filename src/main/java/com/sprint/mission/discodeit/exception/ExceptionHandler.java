package com.sprint.mission.discodeit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Arrays;

@Slf4j
@RestControllerAdvice
public class ExceptionHandler {

  @org.springframework.web.bind.annotation.ExceptionHandler(BaseException.class)
  public ResponseEntity<ErrorResponse> handleServiceException(BaseException exception) {
    ErrorResponse response = new ErrorResponse(exception.getStatus(), exception.getMessage());
    return new ResponseEntity<>(response, exception.getStatus());
  }

  @ResponseBody
  @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
  public String exceptCommon(Exception ex) {
    log.error("---------------------------------------------");
    log.error(ex.getMessage());

    StringBuilder buffer = new StringBuilder("<ul>");

    buffer.append("<li>" + ex.getMessage() + "</li>");

    Arrays.stream(ex.getStackTrace()).forEach(stackTraceElement -> {
      buffer.append("<li>" + stackTraceElement.toString() + "</li>");
    });

    buffer.append("</ul>");

    return buffer.toString();
  }

  @org.springframework.web.bind.annotation.ExceptionHandler(NoHandlerFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public String notFound() {
    return "custom404";
  }
}
