package com.codemaniac.authenticationservice.mapper;

import com.codemaniac.authenticationservice.dto.PermissionDTO;
import com.codemaniac.authenticationservice.model.Permission;
import jakarta.annotation.Nonnull;

public class PermissionMapper {

  private PermissionMapper(){}
  public static PermissionDTO toDTO(@Nonnull final Permission permission) {
    final PermissionDTO permissionDTO = new PermissionDTO();
    permissionDTO.setId(permission.getId());
    permissionDTO.setResource(ResourceMapper.toDTO(permission.getResource()));
    permissionDTO.setAction(permission.getAction());
    return permissionDTO;
  }

  public static Permission toEntity(@Nonnull final PermissionDTO permissionDTO) {
    final Permission permission = new Permission();
    permission.setId(permissionDTO.getId());
    permission.setAction(permissionDTO.getAction());
    permission.setResource(ResourceMapper.toEntity(permissionDTO.getResource()));
    return permission;
  }
}
