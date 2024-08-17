package com.codemaniac.authenticationservice.model;

import java.util.List;
import lombok.Data;

@Data
public class UserRegistrationRequest {
  private String logonId;
  private String password;
  private Role role;
  private List<Long> applicationIds;

  public UserRegistrationRequest() {
  }

  public UserRegistrationRequest(String logonId, String password, Role role, List<Long> applicationIds) {
    this.logonId = logonId;
    this.password = password;
    this.role = role;
    this.applicationIds = applicationIds;
  }
}
