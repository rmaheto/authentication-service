package com.codemaniac.authenticationservice.dto;

import com.codemaniac.authenticationservice.model.Role;
import java.util.Set;
import lombok.Data;
@Data
public class UserPermissionDTO {
    private Long id;
    private String logonId;
    private boolean enabled;
    private Role role;
    private Set<PermissionDTO> permissions;
}
