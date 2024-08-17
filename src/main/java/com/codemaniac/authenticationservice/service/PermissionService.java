package com.codemaniac.authenticationservice.service;

import com.codemaniac.authenticationservice.dto.PermissionDTO;
import com.codemaniac.authenticationservice.model.Action;
import com.codemaniac.authenticationservice.model.Permission;

import java.util.List;
import java.util.Optional;

public interface PermissionService {

  List<PermissionDTO> getPermissions(Optional<Long> userId, Optional<Long> appId);
  PermissionDTO getPermissionById(Long id);
  PermissionDTO createPermission(PermissionDTO permissionDTO);
  PermissionDTO updatePermission(Long id, PermissionDTO permissionDTO);
  void deletePermission(Long id);
}
