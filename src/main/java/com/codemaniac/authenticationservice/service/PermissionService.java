package com.codemaniac.authenticationservice.service;

import com.codemaniac.authenticationservice.dto.PermissionDTO;
import com.codemaniac.authenticationservice.model.Action;
import com.codemaniac.authenticationservice.model.PermissionUpdateRequest;
import java.util.List;
import java.util.Optional;

public interface PermissionService {

  List<PermissionDTO> getPermissions(Optional<Long> userId, Optional<Long> appId);
  PermissionDTO getPermissionById(Long id);
  PermissionDTO createPermission(PermissionDTO permissionDTO);
  PermissionDTO updatePermission(Long id, PermissionDTO permissionDTO);
  void updatePermissionsForUser(Long userId, Long resourceId, Action updatedAction);
  void updatePermissionsForUser(Long userId, List<PermissionUpdateRequest> permissionUpdateRequests);
  void deletePermission(Long id);
}
