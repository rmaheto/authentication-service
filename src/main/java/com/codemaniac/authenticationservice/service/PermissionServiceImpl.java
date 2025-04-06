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
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

  private final PermissionRepository permissionRepository;

  private final UserRepository userRepository;
  private final ResourceRepository resourceRepository;

  private final ApplicationRepository applicationRepository;

  @Override
  public PermissionDTO getPermissionById(final Long id) {
    final Permission permission = permissionRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
    return PermissionMapper.toDTO(permission);
  }

  @Override
  public List<PermissionDTO> getPermissions(final Optional<Long> userId, final Optional<Long> appId) {
    if (userId.isPresent() && appId.isPresent()) {
      return getPermissionsByUserAndApp(userId.get(), appId.get());
    } else if (userId.isPresent()) {
      return getPermissionsByUser(userId.get());
    } else if (appId.isPresent()) {
      return getPermissionsByApp(appId.get());
    } else {
      return permissionRepository.findAll().stream()
          .map(PermissionMapper::toDTO)
          .toList();
    }
  }

  @Override
  public PermissionDTO createPermission(final PermissionDTO permissionDTO) {
    final Permission permission = PermissionMapper.toEntity(permissionDTO);
    final Permission savedPermission = permissionRepository.save(permission);
    return PermissionMapper.toDTO(savedPermission);
  }

  @Override
  public PermissionDTO updatePermission(final Long id, final PermissionDTO permissionDTO) {
    final Permission permission = permissionRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));

    permission.setAction(permissionDTO.getAction());
    permission.setResource(ResourceMapper.toEntity(permissionDTO.getResource()));
    final Permission updatedPermission = permissionRepository.save(permission);
    return PermissionMapper.toDTO(updatedPermission);
  }

  @Transactional
  public void updatePermissionsForUser(final Long userId, final Long resourceId, final Action updatedAction) {
    final User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

    final Resource resource = resourceRepository.findById(resourceId)
        .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + resourceId));

    final Permission permission = permissionRepository.findByUserAndResource(user, resource)
        .orElseThrow(() -> new ResourceNotFoundException("Permission not found for user and resource"));

    permission.setAction(updatedAction);

    permissionRepository.save(permission);
  }

  @Transactional
  @Override
  public void updatePermissionsForUser(@Nonnull final Long userId, @Nonnull final List<PermissionUpdateRequest> permissionUpdates) {

    final User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

    for (final PermissionUpdateRequest updateRequest : permissionUpdates) {

      final Resource resource = resourceRepository.findById(updateRequest.getResourceId())
          .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + updateRequest.getResourceId()));

      final Permission permission = user.getPermissions().stream()
          .filter(p -> p.getResource().equals(resource))
          .findFirst()
          .orElseGet(() -> {
            final Permission newPermission = new Permission();
            newPermission.setResource(resource);
            newPermission.setAction(new Action()); // Default values
            user.getPermissions().add(newPermission);
            return newPermission;
          });

      permission.setAction(updateRequest.getUpdatedAction());
    }

    userRepository.save(user);
  }

  @Override
  public void deletePermission(final Long id) {
    final Permission permission = permissionRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
    permissionRepository.delete(permission);
  }

  private List<PermissionDTO> getPermissionsByUser(@Nonnull final Long userId) {
    final User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    return user.getPermissions().stream()
        .map(PermissionMapper::toDTO)
        .toList();
  }

  private List<PermissionDTO> getPermissionsByApp(@Nonnull final Long appId) {
    final Application application = applicationRepository.findById(appId)
        .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
    return permissionRepository.findAll().stream()
        .filter(permission -> permission.getResource().getApplication().equals(application))
        .map(PermissionMapper::toDTO)
        .toList();
  }

  private List<PermissionDTO> getPermissionsByUserAndApp(@Nonnull final Long userId, @Nonnull final Long appId) {
    final User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    final Application application = applicationRepository.findById(appId)
        .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
    return user.getPermissions().stream()
        .filter(permission -> permission.getResource().getApplication().equals(application))
        .map(PermissionMapper::toDTO)
        .toList();
  }
}
