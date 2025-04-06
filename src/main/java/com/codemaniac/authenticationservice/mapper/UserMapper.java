package com.codemaniac.authenticationservice.mapper;

import com.codemaniac.authenticationservice.dto.ApplicationDTO;
import com.codemaniac.authenticationservice.dto.PermissionDTO;
import com.codemaniac.authenticationservice.dto.UserDTO;
import com.codemaniac.authenticationservice.model.User;
import jakarta.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {

  private UserMapper(){}
  public static UserDTO toDTO(@Nonnull final User user) {
    final UserDTO dto = new UserDTO();
    dto.setId(user.getId());
    dto.setLogonId(user.getLogonId());
    dto.setRole(user.getRole());
    dto.setEnabled(user.isEnabled());

    final Set<ApplicationDTO> applicationDTOS = (user.getPermissions() != null ?
        user.getApplications().stream()
            .map(ApplicationMapper::toDTO)
            .collect(Collectors.toSet()) :
        Collections.emptySet());

    dto.setApplications(applicationDTOS);

    return dto;
  }
}
