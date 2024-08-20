package com.codemaniac.authenticationservice.exception;

public class S3PropertyLoadException extends RuntimeException {

  public S3PropertyLoadException(String message) {
    super(message);
  }

  public S3PropertyLoadException(String message, Throwable cause) {
    super(message, cause);
  }
}
