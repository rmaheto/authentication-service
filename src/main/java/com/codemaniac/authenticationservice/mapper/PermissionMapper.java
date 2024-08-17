package com.codemaniac.authenticationservice.mapper;

import com.codemaniac.authenticationservice.dto.PermissionDTO;
import com.codemaniac.authenticationservice.model.Permission;

public class PermissionMapper {

  public static PermissionDTO toDTO(Permission permission) {
    PermissionDTO permissionDTO = new PermissionDTO();
    permissionDTO.setId(permission.getId());
    permissionDTO.setResource(ResourceMapper.toDTO(permission.getResource()));
    permissionDTO.setAction(permission.getAction());
    return permissionDTO;
  }

}
