package com.codemaniac.authenticationservice.mapper;

import com.codemaniac.authenticationservice.dto.PermissionDTO;
import com.codemaniac.authenticationservice.model.Permission;

public class PermissionMapper {

  private PermissionMapper(){}
  public static PermissionDTO toDTO(Permission permission) {
    PermissionDTO permissionDTO = new PermissionDTO();
    permissionDTO.setId(permission.getId());
    permissionDTO.setResource(ResourceMapper.toDTO(permission.getResource()));
    permissionDTO.setAction(permission.getAction());
    return permissionDTO;
  }

  public static Permission toEntity(PermissionDTO permissionDTO) {
    Permission permission = new Permission();
    permission.setId(permissionDTO.getId());
    permission.setAction(permissionDTO.getAction());
    permission.setResource(ResourceMapper.toEntity(permissionDTO.getResource()));
    return permission;
  }
}
