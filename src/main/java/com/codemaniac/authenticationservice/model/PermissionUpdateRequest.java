package com.codemaniac.authenticationservice.model;

import lombok.Data;

@Data
public class PermissionUpdateRequest {
  private Long resourceId;
  private Action updatedAction;
}
