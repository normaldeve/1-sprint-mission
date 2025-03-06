package com.sprint.mission.discodeit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class MyExceptionHandler {

  @ExceptionHandler(BaseException.class)
  public ResponseEntity<ErrorResponse> handleServiceException(BaseException exception) {
    ErrorResponse response = new ErrorResponse(exception.getStatus(), exception.getMessage());
    return new ResponseEntity<>(response, exception.getStatus());
  }

  @ResponseBody
  @ExceptionHandler(Exception.class)
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

  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<String> notFound(NoHandlerFoundException ex) {
    log.error("404 Error: " + ex.getMessage());
    return new ResponseEntity<>("custom404", HttpStatus.NOT_FOUND);
  }

  // 객체 바인딩 예외 처리
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    log.error("Validation error: ", ex);
    Map<String, String> errors = new HashMap<>();

    ex.getBindingResult().getFieldErrors().forEach(error -> {
      errors.put(error.getField(), error.getDefaultMessage());
    });

    return ResponseEntity.badRequest().body(errors);
  }
}
