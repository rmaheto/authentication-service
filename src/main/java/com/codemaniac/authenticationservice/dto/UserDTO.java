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
  private Set<ApplicationDTO> applications;

  // Default constructor
  public UserDTO() {
  }
  public UserDTO(final Long id, final String logonId, final boolean enabled, final Role role) {
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
