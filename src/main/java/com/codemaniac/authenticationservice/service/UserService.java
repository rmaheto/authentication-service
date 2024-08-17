package com.codemaniac.authenticationservice.service;

import com.codemaniac.authenticationservice.dto.UserDTO;
import com.codemaniac.authenticationservice.model.AuthenticationRequest;
import com.codemaniac.authenticationservice.model.Permission;
import com.codemaniac.authenticationservice.model.Role;
import com.codemaniac.authenticationservice.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    UserDTO registerUser(AuthenticationRequest authenticationRequest);
    UserDTO findByLogonId(String logonId);
    Optional<UserDTO> findById(Long userId);
    void assignPermissionsToUser(UserDTO user, Set<Permission> permissions);
    void updateUserStatus(Long userId, boolean enabled);
    List<UserDTO> findAll();
}
