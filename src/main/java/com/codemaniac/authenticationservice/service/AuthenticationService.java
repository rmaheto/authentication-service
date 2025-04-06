package com.codemaniac.authenticationservice.service;

import com.codemaniac.authenticationservice.model.AuthenticationRequest;
import com.codemaniac.authenticationservice.model.AuthenticationResponse;
import jakarta.annotation.Nonnull;

public interface AuthenticationService {

  AuthenticationResponse authenticate(@Nonnull AuthenticationRequest authenticationRequest);

}
