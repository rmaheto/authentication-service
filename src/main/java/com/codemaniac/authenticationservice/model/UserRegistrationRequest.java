package com.codemaniac.authenticationservice.model;

import java.util.Set;
import lombok.Data;

@Data
public class UserRegistrationRequest {
  private String logonId;
  private String password;
  private Role role;
  private boolean enabled;
  private Set<Long> applicationIds;

}
