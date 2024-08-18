package com.codemaniac.authenticationservice.service;

import com.codemaniac.authenticationservice.dto.ApplicationMapper;
import com.codemaniac.authenticationservice.dto.PermissionDTO;
import com.codemaniac.authenticationservice.dto.ResourceDTO;
import com.codemaniac.authenticationservice.exception.ResourceNotFoundException;
import com.codemaniac.authenticationservice.model.Action;
import com.codemaniac.authenticationservice.model.Application;
import com.codemaniac.authenticationservice.model.Permission;
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
    return convertToDTO(permission);
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
              .map(this::convertToDTO)
              .toList();
    }
  }

  @Override
  public PermissionDTO createPermission(PermissionDTO permissionDTO) {
    Permission permission = convertToEntity(permissionDTO);
    Permission savedPermission = permissionRepository.save(permission);
    return convertToDTO(savedPermission);
  }

  @Override
  public PermissionDTO updatePermission(Long id, PermissionDTO permissionDTO) {
    Permission permission = permissionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));

    permission.setAction(permissionDTO.getAction());
    permission.setResource(convertToEntity(permissionDTO.getResource()));
    Permission updatedPermission = permissionRepository.save(permission);
    return convertToDTO(updatedPermission);
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

  @Override
  public void deletePermission(Long id) {
    Permission permission = permissionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
    permissionRepository.delete(permission);
  }

  private PermissionDTO convertToDTO(Permission permission) {
    ResourceDTO resourceDTO = new ResourceDTO(
            permission.getResource().getId(),
            permission.getResource().getName(),
            ApplicationMapper.convertToDTO(permission.getResource().getApplication())
    );
    return new PermissionDTO(
            permission.getId(),
            resourceDTO,
            permission.getAction()
    );
  }

  private Permission convertToEntity(PermissionDTO permissionDTO) {
    Permission permission = new Permission();
    permission.setId(permissionDTO.getId());
    permission.setAction(permissionDTO.getAction());
    permission.setResource(convertToEntity(permissionDTO.getResource()));
    return permission;
  }

  private Resource convertToEntity(ResourceDTO resourceDTO) {
    Resource resource = new Resource();
    resource.setId(resourceDTO.getId());
    resource.setName(resourceDTO.getName());

    return resource;
  }

  private List<PermissionDTO> getPermissionsByUser(Long userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    return user.getPermissions().stream()
            .map(this::convertToDTO)
            .toList();
  }

  private List<PermissionDTO> getPermissionsByApp(Long appId) {
    Application application = applicationRepository.findById(appId)
            .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
    return permissionRepository.findAll().stream()
            .filter(permission -> permission.getResource().getApplication().equals(application))
            .map(this::convertToDTO)
            .toList();
  }

  private List<PermissionDTO> getPermissionsByUserAndApp(Long userId, Long appId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    Application application = applicationRepository.findById(appId)
            .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
    return user.getPermissions().stream()
            .filter(permission -> permission.getResource().getApplication().equals(application))
            .map(this::convertToDTO)
            .toList();
  }
}
