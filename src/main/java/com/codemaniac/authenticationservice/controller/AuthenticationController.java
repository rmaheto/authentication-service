package com.codemaniac.authenticationservice.controller;

import com.codemaniac.authenticationservice.exception.AuthenticationException;
import com.codemaniac.authenticationservice.model.AuthenticationRequest;
import com.codemaniac.authenticationservice.model.AuthenticationResponse;
import com.codemaniac.authenticationservice.model.ErrorResponse;
import com.codemaniac.authenticationservice.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

  private static final org.slf4j.Logger Logger = LoggerFactory.getLogger("com.codemaniac.security");

  private final AuthenticationService authenticationService;

  @PostMapping("/authenticate")
  public ResponseEntity<?> createAuthenticationToken(
      @RequestBody AuthenticationRequest authenticationRequest) {
    try {
      AuthenticationResponse response = authenticationService.authenticate(authenticationRequest);
      return ResponseEntity.ok(response);
    } catch (AuthenticationException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ErrorResponse("Unauthorized", e.getMessage()));
    } catch (BadCredentialsException e) {
      Logger.warn("Authentication failed for user '{}': Invalid credentials",
          authenticationRequest.getLogonId());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ErrorResponse("Unauthorized", "Incorrect username or password"));
    }
  }
}
