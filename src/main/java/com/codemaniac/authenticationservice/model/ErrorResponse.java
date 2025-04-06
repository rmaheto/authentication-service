package com.codemaniac.authenticationservice.model;

import lombok.Data;

@Data
public class ErrorResponse {

  private String error;
  private String message;

  public ErrorResponse(final String error, final String message) {
    this.error = error;
    this.message = message;
  }
}
