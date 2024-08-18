package com.codemaniac.authenticationservice.service;

import com.codemaniac.authenticationservice.exception.AuthenticationException;
import com.codemaniac.authenticationservice.model.AuthenticationRequest;
import com.codemaniac.authenticationservice.model.AuthenticationResponse;
import com.codemaniac.authenticationservice.model.User;
import com.codemaniac.authenticationservice.repository.UserRepository;
import com.codemaniac.authenticationservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;
  private final ApplicationService applicationService;

  @Override
  public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest)
      throws BadCredentialsException {

    if (!applicationService.existsByDomain(authenticationRequest.getAudience())) {
      throw new AuthenticationException("Invalid audience");
    }

    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            authenticationRequest.getLogonId(), authenticationRequest.getPassword()
        )
    );

    User user = userRepository.findByLogonId(authenticationRequest.getLogonId());

    if (!user.isEnabled()) {
      throw new AuthenticationException("User is disabled");
    }

    final String jwt = jwtUtil.generateToken(user, authenticationRequest.getAudience());

    return new AuthenticationResponse(jwt);
  }
}
