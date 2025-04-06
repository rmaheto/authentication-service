package com.codemaniac.authenticationservice.mapper;

import com.codemaniac.authenticationservice.dto.ApplicationDTO;
import com.codemaniac.authenticationservice.dto.UserDTO;
import com.codemaniac.authenticationservice.dto.UserPermissionDTO;
import com.codemaniac.authenticationservice.model.User;
import jakarta.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class UserPermissionMapper {

  private UserPermissionMapper(){}
  public static UserPermissionDTO toDTO(@Nonnull final User user) {
    final UserPermissionDTO dto = new UserPermissionDTO();
    dto.setId(user.getId());
    dto.setLogonId(user.getLogonId());
    dto.setRole(user.getRole());
    dto.setEnabled(user.isEnabled());

    // Convert permissions to PermissionDTOs
//    Set<ApplicationDTO> applicationDTOS = (user.getPermissions() != null ?
//        user.getApplications().stream()
//            .map(ApplicationMapper::toDTO)
//            .collect(Collectors.toSet()) :
//        Collections.emptySet());
//
//    dto.setApplications(applicationDTOS);

    return dto;
  }
}
