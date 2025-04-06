package com.codemaniac.authenticationservice.exception;

public class UserAlreadyExistsException extends RuntimeException {
  public UserAlreadyExistsException(final String message) {
    super(message);
  }
}
