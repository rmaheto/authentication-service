package com.codemaniac.authenticationservice.dto;

import com.codemaniac.authenticationservice.model.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UserDTO {
  private Long id;
  private String logonId;
  private boolean enabled;
  private Role role;
  private Set<PermissionDTO> permissionDTOS;

  // Default constructor
  public UserDTO() {
  }
  public UserDTO(Long id, String logonId, boolean enabled, Role role) {
    this.id = id;
    this.logonId = logonId;
    this.enabled = enabled;
    this.role = role;
  }

  @Override
  public String toString() {
    return "UserDTO{" +
            "id=" + id +
            ", logonId='" + logonId + '\'' +
            ", enabled=" + enabled +
            ", role=" + role +
            '}';
  }

}
