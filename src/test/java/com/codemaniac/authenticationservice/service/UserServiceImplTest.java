package com.codemaniac.authenticationservice.service;

import com.codemaniac.authenticationservice.dto.UserDTO;
import com.codemaniac.authenticationservice.dto.UserPermissionDTO;
import com.codemaniac.authenticationservice.exception.ResourceNotFoundException;
import com.codemaniac.authenticationservice.exception.UserAlreadyExistsException;
import com.codemaniac.authenticationservice.exception.UserNotFoundException;
import com.codemaniac.authenticationservice.model.Application;
import com.codemaniac.authenticationservice.model.Permission;
import com.codemaniac.authenticationservice.model.Resource;
import com.codemaniac.authenticationservice.model.Role;
import com.codemaniac.authenticationservice.model.User;
import com.codemaniac.authenticationservice.model.UserRegistrationRequest;
import com.codemaniac.authenticationservice.repository.ApplicationRepository;
import com.codemaniac.authenticationservice.repository.PermissionRepository;
import com.codemaniac.authenticationservice.repository.ResourceRepository;
import com.codemaniac.authenticationservice.repository.UserRepository;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock private UserRepository mockUserRepository;

  @Mock private PasswordEncoder mockPasswordEncoder;

  @Mock private ApplicationRepository mockApplicationRepository;

  @Mock private ResourceRepository mockResourceRepository;

  @Mock private PermissionRepository mockPermissionRepository;

  @Captor private ArgumentCaptor<User> userCaptor;

  @InjectMocks private UserServiceImpl underTest;

  private static final Long USER_ID = 1L;
  private static final Long APP_ID = 101L;
  private static final Long APP_ID_2 = 102L;
  private static final String LOGON_ID = "test_user";
  private static final String PASSWORD = "securePassword";
  private static final String ENCODED_PASSWORD = "encodedPassword";

  @Test
  void findById_withValidId_shouldReturnUserDTO() {

    final Long userId = 1L;
    final User mockUser = new User();
    mockUser.setId(userId);
    mockUser.setLogonId(LOGON_ID);

    when(mockUserRepository.findById(userId)).thenReturn(Optional.of(mockUser));

    final var result = underTest.findById(userId);

    Assertions.assertNotNull(result);
    assertEquals(LOGON_ID, result.getLogonId());
  }

  @Test
  void findById_withInvalidId_shouldThrowUserNotFoundException() {

    final Long userId = 99L;

    when(mockUserRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> underTest.findById(userId));
  }

  @Test
  void findByLogonId_withValidId_shouldReturnUserDTO() {
    when(mockUserRepository.findByLogonId(anyString())).thenReturn(new User());

    final var result = underTest.findByLogonId(LOGON_ID);
    assertNotNull(result);
  }

  @Test
  void findByLogonId_withInvalidId_shouldThrowUserNotFoundException() {
    when(mockUserRepository.findByLogonId(anyString())).thenReturn(null);

    final var result = underTest.findByLogonId(LOGON_ID);

    assertNull(result);
  }

  @Test
  void registerUser_withExistingLogonId_shouldThrowUserAlreadyExistsException() {

    final UserRegistrationRequest request = new UserRegistrationRequest();
    request.setLogonId(LOGON_ID);

    when(mockUserRepository.existsByLogonId(LOGON_ID)).thenReturn(true);

    assertThrows(UserAlreadyExistsException.class, () -> underTest.registerUser(request));
  }

  @Test
  void registerUser_withInvalidApplicationIdInRequest_shouldThrowResourceNotFoundException() {

    final UserRegistrationRequest request = new UserRegistrationRequest();
    request.setLogonId(LOGON_ID);
    request.setApplicationIds(Set.of(APP_ID, APP_ID_2));

    when(mockUserRepository.existsByLogonId(LOGON_ID)).thenReturn(false);
    when(mockApplicationRepository.findAllById(anyCollection()))
        .thenReturn(Collections.singletonList(buildApplication(APP_ID_2)));

    assertThrows(ResourceNotFoundException.class, () -> underTest.registerUser(request));
  }

  @Test
  void registerUser_withNewUser_shouldReturnUserDTO() {

    final UserRegistrationRequest request = new UserRegistrationRequest();
    request.setLogonId(LOGON_ID);
    request.setPassword(PASSWORD);
    request.setRole(Role.USER);
    request.setApplicationIds(Set.of(APP_ID));

    when(mockUserRepository.existsByLogonId(anyString())).thenReturn(false);
    when(mockPasswordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);
    when(mockApplicationRepository.findAllById(anyCollection()))
        .thenReturn(Collections.singletonList(buildApplication(APP_ID)));

    final User savedUser = new User();
    savedUser.setLogonId(LOGON_ID);
    savedUser.setPassword(ENCODED_PASSWORD);
    savedUser.setRole(Role.USER);

    when(mockUserRepository.save(any(User.class))).thenReturn(savedUser);

    final var result = underTest.registerUser(request);

    Assertions.assertNotNull(result);
    assertEquals(LOGON_ID, result.getLogonId());
  }

  @Test
  void updateUser_withRemovedApplication_shouldRemoveApplicationAndPermissions() {

    final Application app = new Application();
    app.setId(APP_ID);

    final Resource resource = new Resource();
    resource.setApplication(app);

    final Permission permission = new Permission();
    permission.setResource(resource);

    final User user = new User();
    user.setId(USER_ID);
    user.setApplications(new HashSet<>(Set.of(app)));
    user.setPermissions(new HashSet<>(Set.of(permission)));

    final UserRegistrationRequest request = new UserRegistrationRequest();
    request.setLogonId(LOGON_ID);
    request.setEnabled(true);
    request.setRole(Role.USER);
    request.setApplicationIds(Set.of()); // simulate removal
    when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.of(user));

    underTest.updateUser(USER_ID, request);

    Assertions.assertTrue(user.getApplications().isEmpty(), "Application should be removed");
    Assertions.assertTrue(
        user.getPermissions().isEmpty(),
        "Permissions related to the removed application should be removed");
    verify(mockUserRepository).saveAndFlush(user);
  }

  @Test
  void updateUser_withAddedApplication_shouldAddApplicationAndPermissions() {

    final Application app = new Application();
    app.setId(APP_ID);

    final Application newApp = new Application();
    newApp.setId(APP_ID_2);

    final Resource resource = new Resource();
    resource.setApplication(app);

    final Permission permission = new Permission();
    permission.setResource(resource);

    final User user = new User();
    user.setId(USER_ID);
    user.setApplications(new HashSet<>(Set.of(app)));
    user.setPermissions(new HashSet<>(Set.of(permission)));

    final UserRegistrationRequest request = new UserRegistrationRequest();
    request.setLogonId(LOGON_ID);
    request.setEnabled(true);
    request.setRole(Role.USER);
    request.setApplicationIds(Set.of(APP_ID, APP_ID_2));
    when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(mockApplicationRepository.findById(APP_ID_2)).thenReturn(Optional.of(newApp));

    underTest.updateUser(USER_ID, request);

    verify(mockUserRepository).saveAndFlush(userCaptor.capture());
    final User capturedUser = userCaptor.getValue();

    assertEquals(
        2, capturedUser.getApplications().size(), "User should have 2 applications assigned");
  }

  @Test
  void updateUser_withNonExistingUser_shouldThrowException() {

    final UserRegistrationRequest request = new UserRegistrationRequest();

    when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> underTest.updateUser(USER_ID, request));
  }

  @Test
  void updateUserStatus_withValidUser_shouldUpdateStatus() {

    final User user = new User();
    user.setId(USER_ID);
    user.setEnabled(false);

    when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.of(user));

    underTest.updateUserStatus(USER_ID, true);

    verify(mockUserRepository).saveAndFlush(user);
    Assertions.assertTrue(user.isEnabled());
  }

  @Test
  void updateUserStatus_withInvalidUser_shouldThrowException() {

    when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> underTest.updateUserStatus(USER_ID, true));
  }

  @Test
  void findUserPermissions_withValidUserAndApp_shouldReturnFilteredPermissions() {

    final Application application = new Application();
    application.setId(APP_ID);

    final Resource resource = new Resource();
    resource.setApplication(application);

    final Permission permission = new Permission();
    permission.setResource(resource);

    final User user = new User();
    user.setPermissions(Set.of(permission));

    when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.of(user));

    final UserPermissionDTO result = underTest.findUserPermissions(USER_ID, APP_ID);

    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.getPermissions().isEmpty());
  }

  @Test
  void findUserPermissions_withUserNotFound_throwUserNotFoundException() {
    when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> underTest.findUserPermissions(USER_ID, APP_ID));
  }

  @Test
  void findAllUserPermissions_withValidUser_shouldReturnAllPermissions() {

    final Permission permission = new Permission();
    final Resource resource = new Resource();
    resource.setApplication(new Application());
    permission.setResource(resource);
    final User user = new User();
    user.setPermissions(Set.of(permission));

    when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.of(user));

    final UserPermissionDTO result = underTest.findAllUserPermissions(USER_ID);

    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.getPermissions().isEmpty());
  }

  @Test
  void assignApplicationToUser_withValidInput_shouldAssignAndReturnUserDTO() {

    final Application application = new Application();
    application.setId(APP_ID);
    application.setResources(Set.of());

    final User user = new User();
    user.setId(USER_ID);
    user.setApplications(new HashSet<>());
    user.setPermissions(new HashSet<>());

    when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(mockApplicationRepository.findById(APP_ID)).thenReturn(Optional.of(application));

    final UserDTO result = underTest.assignApplicationToUser(USER_ID, APP_ID);

    Assertions.assertNotNull(result);
    verify(mockUserRepository).saveAndFlush(user);
  }

  private Application buildApplication(final Long id) {
    final Application application = new Application();
    application.setId(id);

    final Resource resource = new Resource();
    application.setResources(Set.of(resource));

    return application;
  }
}
