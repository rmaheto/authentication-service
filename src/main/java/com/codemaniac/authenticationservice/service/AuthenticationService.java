package com.codemaniac.authenticationservice.service;

import com.codemaniac.authenticationservice.model.AuthenticationRequest;
import com.codemaniac.authenticationservice.model.AuthenticationResponse;

public interface AuthenticationService {

  AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);

}
