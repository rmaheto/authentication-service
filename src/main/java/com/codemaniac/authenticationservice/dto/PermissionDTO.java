package com.codemaniac.authenticationservice.dto;

import com.codemaniac.authenticationservice.model.Action;
import lombok.Data;

@Data
public class PermissionDTO {

  private Long id;
  private ResourceDTO resource;
  private Action action;
//  private Set<UserDTO> users;


  public PermissionDTO() {
  }

  // Parameterized constructor
  public PermissionDTO(Long id, ResourceDTO resource, Action action) {
    this.id = id;
    this.resource = resource;
    this.action = action;
//    this.users = users;
  }

  @Override
  public String toString() {
    return "PermissionDTO{" +
        "id=" + id +
        ", resource=" + resource +
        ", action=" + action +
//            ", users=" + users +
        '}';
  }
}
