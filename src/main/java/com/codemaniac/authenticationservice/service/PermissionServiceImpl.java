package com.codemaniac.authenticationservice.service;

import com.codemaniac.authenticationservice.dto.PermissionDTO;
import com.codemaniac.authenticationservice.exception.ResourceNotFoundException;
import com.codemaniac.authenticationservice.mapper.PermissionMapper;
import com.codemaniac.authenticationservice.mapper.ResourceMapper;
import com.codemaniac.authenticationservice.model.Action;
import com.codemaniac.authenticationservice.model.Application;
import com.codemaniac.authenticationservice.model.Permission;
import com.codemaniac.authenticationservice.model.PermissionUpdateRequest;
import com.codemaniac.authenticationservice.model.Resource;
import com.codemaniac.authenticationservice.model.User;
import com.codemaniac.authenticationservice.repository.ApplicationRepository;
import com.codemaniac.authenticationservice.repository.PermissionRepository;
import com.codemaniac.authenticationservice.repository.ResourceRepository;
import com.codemaniac.authenticationservice.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService{

    private final PermissionRepository permissionRepository;

  private final UserRepository userRepository;
  private final ResourceRepository resourceRepository;

  private final ApplicationRepository applicationRepository;
  @Override
  public PermissionDTO getPermissionById(Long id) {
    Permission permission = permissionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
    return PermissionMapper.toDTO(permission);
  }

  @Override
  public List<PermissionDTO> getPermissions(Optional<Long> userId, Optional<Long> appId) {
    if (userId.isPresent() && appId.isPresent()) {
      // Get permissions by both user and app
      return getPermissionsByUserAndApp(userId.get(), appId.get());
    } else if (userId.isPresent()) {
      // Get permissions by user
      return getPermissionsByUser(userId.get());
    } else if (appId.isPresent()) {
      // Get permissions by app
      return getPermissionsByApp(appId.get());
    } else {
      // Return all permissions if no criteria provided
      return permissionRepository.findAll().stream()
              .map(PermissionMapper::toDTO)
              .toList();
    }
  }

  @Override
  public PermissionDTO createPermission(PermissionDTO permissionDTO) {
    Permission permission = PermissionMapper.toEntity(permissionDTO);
    Permission savedPermission = permissionRepository.save(permission);
    return PermissionMapper.toDTO(savedPermission);
  }

  @Override
  public PermissionDTO updatePermission(Long id, PermissionDTO permissionDTO) {
    Permission permission = permissionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));

    permission.setAction(permissionDTO.getAction());
    permission.setResource(ResourceMapper.toEntity(permissionDTO.getResource()));
    Permission updatedPermission = permissionRepository.save(permission);
    return PermissionMapper.toDTO(updatedPermission);
  }

  @Transactional
  public void updatePermissionsForUser(Long userId, Long resourceId, Action updatedAction) {
    // Retrieve the user
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

    // Retrieve the resource
    Resource resource = resourceRepository.findById(resourceId)
        .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + resourceId));

    // Find the specific permission for this user and resource
    Permission permission = permissionRepository.findByUserAndResource(user, resource)
        .orElseThrow(() -> new ResourceNotFoundException("Permission not found for user and resource"));

    // Update the permission's actions
    permission.setAction(updatedAction);

    // Save the updated permission
    permissionRepository.save(permission);
  }

  @Transactional
  @Override
  public void updatePermissionsForUser(Long userId, List<PermissionUpdateRequest> permissionUpdates) {
    // Fetch the user
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

    for (PermissionUpdateRequest updateRequest : permissionUpdates) {
      // Fetch the resource
      Resource resource = resourceRepository.findById(updateRequest.getResourceId())
          .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + updateRequest.getResourceId()));

      // Find the existing permission or create a new one
      Permission permission = user.getPermissions().stream()
          .filter(p -> p.getResource().equals(resource))
          .findFirst()
          .orElseGet(() -> {
            Permission newPermission = new Permission();
            newPermission.setResource(resource);
            newPermission.setAction(new Action()); // Default values
            user.getPermissions().add(newPermission);
            return newPermission;
          });

      // Update the permission's action
      permission.setAction(updateRequest.getUpdatedAction());
    }

    // Save the user with updated permissions
    userRepository.save(user);
  }
  @Override
  public void deletePermission(Long id) {
    Permission permission = permissionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
    permissionRepository.delete(permission);
  }

  private List<PermissionDTO> getPermissionsByUser(Long userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    return user.getPermissions().stream()
            .map(PermissionMapper::toDTO)
            .toList();
  }

  private List<PermissionDTO> getPermissionsByApp(Long appId) {
    Application application = applicationRepository.findById(appId)
            .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
    return permissionRepository.findAll().stream()
            .filter(permission -> permission.getResource().getApplication().equals(application))
            .map(PermissionMapper::toDTO)
            .toList();
  }

  private List<PermissionDTO> getPermissionsByUserAndApp(Long userId, Long appId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    Application application = applicationRepository.findById(appId)
            .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
    return user.getPermissions().stream()
            .filter(permission -> permission.getResource().getApplication().equals(application))
            .map(PermissionMapper::toDTO)
            .toList();
  }
}
