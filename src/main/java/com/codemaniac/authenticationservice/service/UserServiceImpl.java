package com.codemaniac.authenticationservice.service;

import com.codemaniac.authenticationservice.dto.UserDTO;
import com.codemaniac.authenticationservice.exception.ResourceNotFoundException;
import com.codemaniac.authenticationservice.model.AuthenticationRequest;
import com.codemaniac.authenticationservice.model.Permission;
import com.codemaniac.authenticationservice.model.Role;
import com.codemaniac.authenticationservice.model.User;
import com.codemaniac.authenticationservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  @Transactional
  @Override
  public UserDTO registerUser(AuthenticationRequest authenticationRequest) {
    User user = new User();
    user.setLogonId(authenticationRequest.getLogonId());
    user.setPassword(passwordEncoder.encode(authenticationRequest.getPassword()));
    user.setRole(authenticationRequest.getRole());
    user.setEnabled(true);
    User savedUser = userRepository.save(user);
    return convertToDTO(savedUser);
  }

  @Transactional(readOnly = true)
  @Override
  public Optional<UserDTO> findById(Long userId) {
    Optional<User> user = userRepository.findById(userId);
    if (user.isEmpty()) {
      log.warn("User with id: {} not found", userId);
      return Optional.empty();
    }
    return Optional.of(convertToDTO(user.get()));
  }

  @Transactional(readOnly = true)
  @Override
  public List<UserDTO> findAll() {
    return userRepository.findAll().stream().map(this::convertToDTO).toList();
  }


  @Transactional
  @Override
  public void assignPermissionsToUser(UserDTO userDTO, Set<Permission> permissions) {
    User user = convertToEntity(userDTO);
    user.getPermissions().addAll(permissions);
    userRepository.save(user);
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
    return convertToDTO(user);
  }

  private UserDTO convertToDTO(User user) {
    UserDTO dto = new UserDTO();
    dto.setId(user.getId());
    dto.setLogonId(user.getLogonId());
    dto.setRole(user.getRole());
    dto.setEnabled(user.isEnabled());
//    dto.setPermissionIds(user.getPermissions().stream().map(Permission::getId).collect(Collectors.toSet()));
    return dto;
  }

  private User convertToEntity(UserDTO userDTO) {
    User user = new User();
    user.setId(userDTO.getId());
    user.setLogonId(userDTO.getLogonId());
    user.setRole(userDTO.getRole());
    user.setEnabled(userDTO.isEnabled());
//    user.setPermissions(userDTO.get);

//    Set<Permission> permissions = userDTO..stream()
//            .map(id -> permissionRepository.findById(id)
//                    .orElseThrow(() -> new ResourceNotFoundException("Permission not found")))
//            .collect(Collectors.toSet());
//    user.setPermissions(permissions);

    return user;
  }

}
