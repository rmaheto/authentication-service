package com.codemaniac.authenticationservice.service;

import com.codemaniac.authenticationservice.exception.AuthenticationException;
import com.codemaniac.authenticationservice.model.AuthenticationRequest;
import com.codemaniac.authenticationservice.model.AuthenticationResponse;
import com.codemaniac.authenticationservice.model.User;
import com.codemaniac.authenticationservice.repository.UserRepository;
import com.codemaniac.authenticationservice.security.JwtUtil;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

  @Mock
  private JwtUtil mockJwtUtil;

  @Mock
  private AuthenticationManager mockAuthenticationManager;

  @Mock
  private UserRepository mockUserRepository;

  @Mock
  private ApplicationService mockApplicationService;

  @InjectMocks
  private AuthenticationServiceImpl underTest;

  @Test
  void authenticate_withValidAudience_success() {
    final AuthenticationRequest authenticationRequest = buildAuthenticationRequest();
    final User mockUser = buildUser(true);
    final String expectedToken = "token";

    when(mockApplicationService.existsByDomain(anyString())).thenReturn(true);
    when(mockUserRepository.findByLogonId(anyString())).thenReturn(mockUser);
    when(mockJwtUtil.generateToken(any(User.class), anyString())).thenReturn(expectedToken);

    final AuthenticationResponse authenticationResponse = underTest.authenticate(authenticationRequest);

    assertNotNull(authenticationResponse);
    assertEquals(expectedToken, authenticationResponse.jwt());

    verify(mockApplicationService, times(1)).existsByDomain(anyString());
    verify(mockUserRepository, times(1)).findByLogonId(anyString());
    verify(mockJwtUtil, times(1)).generateToken(any(User.class), anyString());
  }

  @Test
  void authenticate_withInvalidAudience_throwsException() {
    final AuthenticationRequest authenticationRequest = buildAuthenticationRequest();

    when(mockApplicationService.existsByDomain(anyString())).thenReturn(false);

    assertThrows(AuthenticationException.class, () -> underTest.authenticate(authenticationRequest));

    verify(mockApplicationService, times(1)).existsByDomain(anyString());
    verifyNoInteractions(mockJwtUtil, mockAuthenticationManager, mockUserRepository);
  }

  @Test
  void authenticate_withDisabledUser_throwsException() {
    final AuthenticationRequest authenticationRequest = buildAuthenticationRequest();
    final User mockUser = buildUser(false);

    when(mockApplicationService.existsByDomain(anyString())).thenReturn(true);
    when(mockUserRepository.findByLogonId(anyString())).thenReturn(mockUser);

    assertThrows(AuthenticationException.class, () -> underTest.authenticate(authenticationRequest));

    verify(mockApplicationService, times(1)).existsByDomain(anyString());
    verify(mockUserRepository, times(1)).findByLogonId(anyString());
    verifyNoInteractions(mockJwtUtil);
  }

  private AuthenticationRequest buildAuthenticationRequest() {
    final AuthenticationRequest authenticationRequest = new AuthenticationRequest();
    authenticationRequest.setAudience("audience");
    authenticationRequest.setLogonId("logonId");
    authenticationRequest.setPassword("password");
    return authenticationRequest;
  }

  private User buildUser(final boolean enabled) {
    final User user = new User();
    user.setEnabled(enabled);
    return user;

  }

}