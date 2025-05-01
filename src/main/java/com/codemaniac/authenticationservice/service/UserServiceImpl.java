package com.codemaniac.authenticationservice.service;

import com.codemaniac.authenticationservice.dto.PermissionDTO;
import com.codemaniac.authenticationservice.dto.UserDTO;
import com.codemaniac.authenticationservice.dto.UserPermissionDTO;
import com.codemaniac.authenticationservice.exception.ResourceNotFoundException;
import com.codemaniac.authenticationservice.exception.UserAlreadyExistsException;
import com.codemaniac.authenticationservice.exception.UserNotFoundException;
import com.codemaniac.authenticationservice.mapper.PermissionMapper;
import com.codemaniac.authenticationservice.mapper.UserMapper;
import com.codemaniac.authenticationservice.mapper.UserPermissionMapper;
import com.codemaniac.authenticationservice.model.Action;
import com.codemaniac.authenticationservice.model.Application;
import com.codemaniac.authenticationservice.model.Permission;
import com.codemaniac.authenticationservice.model.User;
import com.codemaniac.authenticationservice.model.UserRegistrationRequest;
import com.codemaniac.authenticationservice.repository.ApplicationRepository;
import com.codemaniac.authenticationservice.repository.PermissionRepository;
import com.codemaniac.authenticationservice.repository.ResourceRepository;
import com.codemaniac.authenticationservice.repository.UserRepository;
import jakarta.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  private final ApplicationRepository applicationRepository;

  private final ResourceRepository resourceRepository;

  private final PermissionRepository permissionRepository;
  private static final String USER_NOT_FOUND_MSG = "User not found with id: %s";

  @Transactional(readOnly = true)
  @Override
  public UserDTO findById(final Long userId) {
    return userRepository
        .findById(userId)
        .map(UserMapper::toDTO)
        .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND_MSG, userId)));
  }

  @Transactional(readOnly = true)
  @Override
  public List<UserDTO> findAll() {
    return userRepository.findAll().stream().map(UserMapper::toDTO).toList();
  }

  public UserDTO findByLogonId(final String logonId) {
    final User user = userRepository.findByLogonId(logonId);

    if (ObjectUtils.isEmpty(user)) {
      return null;
    }

    return UserMapper.toDTO(user);
  }

  @Transactional
  @Override
  public UserDTO registerUser(final UserRegistrationRequest request) {

    if (userRepository.existsByLogonId(request.getLogonId())) {
      throw new UserAlreadyExistsException("A user with this logonId already exists.");
    }

    User user = new User();
    user.setLogonId(request.getLogonId());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(request.getRole());
    user.setEnabled(true);

    // Assign applications to the user
    final List<Application> applications =
        applicationRepository.findAllById(request.getApplicationIds());

    validateAllApplicationsExist(applications, request.getApplicationIds());
    assignApplicationToUser(user, applications);

    user = userRepository.save(user);
    return UserMapper.toDTO(user);
  }

  @Transactional
  @Override
  public UserDTO updateUser(final Long userId, final UserRegistrationRequest userDetails) {
    final User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    user.setLogonId(userDetails.getLogonId());
    user.setEnabled(userDetails.isEnabled());
    user.setRole(userDetails.getRole());

    final Set<Application> currentApplications = user.getApplications();
    final Set<Long> newApplicationIds = new HashSet<>(userDetails.getApplicationIds());

    // Remove applications that are no longer selected and their associated permissions
    currentApplications.removeIf(
        application -> {
          final boolean isRemoved = !newApplicationIds.contains(application.getId());
          if (isRemoved) {
            removePermissionsForApplication(
                user, application); // Remove permissions related to this application
          }
          return isRemoved;
        });

    // Add new applications and assign default permissions
    for (final Long appId : newApplicationIds) {
      if (currentApplications.stream().noneMatch(app -> app.getId().equals(appId))) {
        final Application application =
            applicationRepository
                .findById(appId)
                .orElseThrow(
                    () -> new ResourceNotFoundException("Application not found with id: " + appId));
        user.getApplications().add(application);
        assignDefaultPermissionsForApplication(user, application);
      }
    }

    // Save the updated user
    userRepository.saveAndFlush(user);

    return UserMapper.toDTO(user);
  }

  @Override
  //  @Transactional
  public void updateUserStatus(final Long userId, final boolean enabled) {
    final User user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () -> new UserNotFoundException(String.format(USER_NOT_FOUND_MSG, userId)));
    user.setEnabled(enabled);
    userRepository.saveAndFlush(user);
  }

  @Override
  @Transactional(readOnly = true)
  public UserPermissionDTO findUserPermissions(
      @Nonnull final Long userId, @Nonnull final Long appId) {
    final Optional<User> user = userRepository.findById(userId);

    if (user.isEmpty()) {
      throw new UserNotFoundException(String.format(USER_NOT_FOUND_MSG, userId));
    }

    final UserPermissionDTO userDTO = UserPermissionMapper.toDTO(user.get());

    // Filter permissions by application ID
    final Set<PermissionDTO> filteredPermissions =
        user.get().getPermissions().stream()
            .filter(permission -> permission.getResource().getApplication().getId().equals(appId))
            .map(PermissionMapper::toDTO)
            .collect(Collectors.toSet());

    userDTO.setPermissions(filteredPermissions);

    return userDTO;
  }

  @Override
  @Transactional(readOnly = true)
  public UserPermissionDTO findAllUserPermissions(@Nonnull final Long userId) {
    final User user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () -> new UserNotFoundException(String.format(USER_NOT_FOUND_MSG, userId)));

    final UserPermissionDTO userDTO = UserPermissionMapper.toDTO(user);

    // Map all permissions
    final Set<PermissionDTO> allPermissions =
        user.getPermissions().stream().map(PermissionMapper::toDTO).collect(Collectors.toSet());

    userDTO.setPermissions(allPermissions);

    return userDTO;
  }

  @Transactional
  @Override
  public UserDTO assignApplicationToUser(@Nonnull final Long userId, @Nonnull final Long appId) {
    final User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

    final Application application =
        applicationRepository
            .findById(appId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Application not found with id: " + appId));

    user.getApplications().add(application);

    assignDefaultPermissionsForApplication(user, application);

    userRepository.saveAndFlush(user);
    return UserMapper.toDTO(user);
  }

  private void assignDefaultPermissionsForApplication(
      @Nonnull final User user, @Nonnull final Application application) {

    final List<Permission> newPermissions =
        application.getResources().stream()
            // Filter out resources for which the user already has permissions
            .filter(
                resource ->
                    user.getPermissions().stream()
                        .noneMatch(permission -> permission.getResource().equals(resource)))
            .map(
                resource -> {
                  final Permission permission = new Permission();
                  permission.setResource(resource);
                  permission.setAction(new Action());
                  return permission;
                })
            .toList();

    // Save the new permissions before associating them with the user
    permissionRepository.saveAll(newPermissions);

    user.getPermissions().addAll(newPermissions);
  }

  private void assignApplicationToUser(
      @Nonnull final User user, @Nonnull final List<Application> applications) {

    user.getApplications().addAll(applications);

    // Automatically assign default permissions for resources of assigned applications
    applications.forEach(
        application ->
            application
                .getResources()
                .forEach(
                    resource -> {
                      final Permission permission = new Permission();
                      permission.setResource(resource);
                      permission.setAction(new Action());
                      user.getPermissions().add(permission);
                    }));
  }

  private void removePermissionsForApplication(
      @Nonnull final User user, @Nonnull final Application application) {
    final Set<Permission> permissionsToRemove =
        user.getPermissions().stream()
            .filter(permission -> permission.getResource().getApplication().equals(application))
            .collect(Collectors.toSet());

    user.getPermissions().removeAll(permissionsToRemove);
  }

  private void validateAllApplicationsExist(
      final List<Application> applications, final Set<Long> requestedAppIds) {

    if (applications.size() != requestedAppIds.size()) {
      final Set<Long> foundIds =
          applications.stream().map(Application::getId).collect(Collectors.toSet());

      final List<Long> missingIds =
          requestedAppIds.stream().filter(id -> !foundIds.contains(id)).toList();

      throw new ResourceNotFoundException("Applications not found with IDs: " + missingIds);
    }
  }
}
