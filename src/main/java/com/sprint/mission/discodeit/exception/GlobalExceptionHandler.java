package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(DiscodeitException.class)
  public ResponseEntity<ErrorResponse> handleServiceException(DiscodeitException exception) {
    ErrorResponse response = new ErrorResponse(exception.getStatus(), exception.getMessage());
    return new ResponseEntity<>(response, exception.getStatus());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleException(IllegalArgumentException e) {
    e.printStackTrace();
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(e.getMessage());
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<String> handleException(NoSuchElementException e) {
    e.printStackTrace();
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleException(Exception e) {
    e.printStackTrace();
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(e.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
    // 가장 첫 번째 에러 메시지 가져오기
    String firstErrorMessage = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .findFirst()
        .map(error -> error.getDefaultMessage())
        .orElse("입력값이 유효하지 않습니다.");

    ErrorResponse errorResponse = new ErrorResponse(
        HttpStatus.BAD_REQUEST,
        firstErrorMessage
    );

    return ResponseEntity.badRequest().body(errorResponse);
  }
}
