package com.codemaniac.authenticationservice.service;

import com.codemaniac.authenticationservice.dto.PermissionDTO;
import com.codemaniac.authenticationservice.dto.UserDTO;
import com.codemaniac.authenticationservice.exception.ResourceNotFoundException;
import com.codemaniac.authenticationservice.mapper.PermissionMapper;
import com.codemaniac.authenticationservice.mapper.UserMapper;
import com.codemaniac.authenticationservice.model.Action;
import com.codemaniac.authenticationservice.model.Application;
import com.codemaniac.authenticationservice.model.Permission;
import com.codemaniac.authenticationservice.model.Resource;
import com.codemaniac.authenticationservice.model.User;
import com.codemaniac.authenticationservice.model.UserRegistrationRequest;
import com.codemaniac.authenticationservice.repository.ApplicationRepository;
import com.codemaniac.authenticationservice.repository.ResourceRepository;
import com.codemaniac.authenticationservice.repository.UserRepository;
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

  @Transactional
  @Override
  public UserDTO registerUser(UserRegistrationRequest request) {

    User user = new User();
    user.setLogonId(request.getLogonId());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(request.getRole());
    user.setEnabled(true);

    // Assign applications to the user
    List<Application> applications = applicationRepository.findAllById(request.getApplicationIds());
    user.getApplications().addAll(applications);

    // Automatically assign default permissions for resources of assigned applications
    User finalUser = user;
    applications.forEach(application -> application.getResources().forEach(resource -> {
      Permission permission = new Permission();
      permission.setResource(resource);
      permission.setAction(new Action());
      finalUser.getPermissions().add(permission);
    }));

    user = userRepository.save(user);
    return UserMapper.toDTO(user);
  }

  @Transactional(readOnly = true)
  @Override
  public Optional<UserDTO> findById(Long userId) {
    return userRepository.findById(userId)
        .map(UserMapper::toDTO);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<UserDTO> findUserPermissionsByApp(Long userId, Long appId) {
    Optional<User> user = userRepository.findById(userId);

    if (user.isEmpty()) {
      return Optional.empty();
    }

    UserDTO userDTO = UserMapper.toDTO(user.get());

    // Filter permissions by application ID
    Set<PermissionDTO> filteredPermissions = user.get().getPermissions().stream()
        .filter(permission -> permission.getResource().getApplication().getId().equals(appId))
        .map(PermissionMapper::toDTO).collect(Collectors.toSet());

    userDTO.setPermissionDTOS(filteredPermissions);

    return Optional.of(userDTO);
  }
  @Transactional(readOnly = true)
  @Override
  public List<UserDTO> findAll() {
    return userRepository.findAll().stream()
        .map(UserMapper::toDTO)
        .toList();
  }

  @Override
  @Transactional
  public void updateUserStatus(Long userId, boolean enabled) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    user.setEnabled(enabled);
  }

  public UserDTO findByLogonId(String logonId) {
    User user = userRepository.findByLogonId(logonId);
    if (ObjectUtils.isEmpty(user)) {
      return null;
    }
    return UserMapper.toDTO(user);
  }

  @Transactional
  @Override
  public void assignApplicationToUser(Long userId, Long appId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

    Application application = applicationRepository.findById(appId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Application not found with id: " + appId));

    user.getApplications().add(application);
    assignDefaultPermissionsForApplication(user, application);

    userRepository.save(user);
  }

  private void assignDefaultPermissionsForApplication(User user, Application application) {
    List<Resource> resources = resourceRepository.findByApplication(application);

    resources.stream()
        // Filter out resources for which the user already has permissions
        .filter(resource -> user.getPermissions().stream()
            .noneMatch(permission -> permission.getResource().equals(resource)))
        // Create new permissions with default values for those resources
        .map(resource -> {
          Permission permission = new Permission();
          permission.setResource(resource);
          permission.setAction(new Action()); // Default values are false
          return permission;
        })
        // Collect and add the new permissions to the user's permissions
        .forEach(user.getPermissions()::add);
  }

}
