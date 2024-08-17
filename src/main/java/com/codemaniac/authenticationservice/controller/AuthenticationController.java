package com.codemaniac.authenticationservice.controller;

import com.codemaniac.authenticationservice.exception.AuthenticationException;
import com.codemaniac.authenticationservice.model.AuthenticationRequest;
import com.codemaniac.authenticationservice.model.AuthenticationResponse;
import com.codemaniac.authenticationservice.model.User;
import com.codemaniac.authenticationservice.repository.UserRepository;
import com.codemaniac.authenticationservice.security.JwtUtil;
import com.codemaniac.authenticationservice.service.ApplicationServiceImpl;
import com.codemaniac.authenticationservice.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationManager authenticationManager;

  private final JwtUtil jwtUtil;

  private final UserRepository userRepository;

  private final ApplicationServiceImpl applicationService;

  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> createAuthenticationToken(
      @RequestBody AuthenticationRequest authenticationRequest) throws Exception {
    try {
//            // Verify the audience (domain)
//            if (!applicationService.existsByDomain(authenticationRequest.getAudience())) {
//                throw new AuthenticationException("Invalid audience");
//            }

      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              authenticationRequest.getLogonId(), authenticationRequest.getPassword())
      );

      User user = userRepository.findByLogonId(authenticationRequest.getLogonId());
      final String jwt = jwtUtil.generateToken(user, authenticationRequest.getAudience());

      return ResponseEntity.ok(new AuthenticationResponse(jwt));

    } catch (BadCredentialsException e) {
      throw new AuthenticationException("Incorrect username or password", e.getCause());
    }
  }
}
