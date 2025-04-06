package com.codemaniac.authenticationservice.exception;

import com.codemaniac.authenticationservice.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(final UserAlreadyExistsException ex) {
    final ErrorResponse errorResponse = new ErrorResponse("User Registration Error", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFoundException(final UserNotFoundException ex) {
    final ErrorResponse errorResponse = new ErrorResponse("User Error", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }
}
