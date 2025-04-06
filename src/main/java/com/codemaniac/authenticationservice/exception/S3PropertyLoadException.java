package com.codemaniac.authenticationservice.exception;

public class S3PropertyLoadException extends RuntimeException {

  public S3PropertyLoadException(final String message) {
    super(message);
  }

  public S3PropertyLoadException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
