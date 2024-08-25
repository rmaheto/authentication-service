package com.codemaniac.authenticationservice.service;

import com.codemaniac.authenticationservice.dto.UserDTO;
import com.codemaniac.authenticationservice.model.AuthenticationRequest;
import com.codemaniac.authenticationservice.model.Permission;
import com.codemaniac.authenticationservice.model.Role;
import com.codemaniac.authenticationservice.model.User;

import com.codemaniac.authenticationservice.model.UserRegistrationRequest;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {

  UserDTO registerUser(UserRegistrationRequest request);

  UserDTO assignApplicationToUser(Long userId, Long appId);

  UserDTO findByLogonId(String logonId);

  UserDTO findById(Long userId);

  UserDTO findUserPermissionsByApp(Long userId, Long appId);

  void updateUserStatus(Long userId, boolean enabled);

  List<UserDTO> findAll();
}
