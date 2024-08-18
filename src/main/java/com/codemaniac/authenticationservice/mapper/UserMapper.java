package com.codemaniac.authenticationservice.mapper;

import com.codemaniac.authenticationservice.dto.PermissionDTO;
import com.codemaniac.authenticationservice.dto.UserDTO;
import com.codemaniac.authenticationservice.model.User;
import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {

  private UserMapper(){}
  public static UserDTO toDTO(User user) {
    UserDTO dto = new UserDTO();
    dto.setId(user.getId());
    dto.setLogonId(user.getLogonId());
    dto.setRole(user.getRole());
    dto.setEnabled(user.isEnabled());

    // Convert permissions to PermissionDTOs
    Set<PermissionDTO> permissionDTOS = user.getPermissions().stream()
        .map(PermissionMapper::toDTO)
        .collect(Collectors.toSet());
    dto.setPermissionDTOS(permissionDTOS);

    return dto;
  }
}
