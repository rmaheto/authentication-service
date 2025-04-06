package com.codemaniac.authenticationservice.controller;

import com.codemaniac.authenticationservice.dto.UserDTO;
import com.codemaniac.authenticationservice.dto.UserPermissionDTO;
import com.codemaniac.authenticationservice.model.PermissionUpdateRequest;
import com.codemaniac.authenticationservice.model.UserRegistrationRequest;
import com.codemaniac.authenticationservice.service.PermissionService;
import com.codemaniac.authenticationservice.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final PermissionService permissionService;

  @PostMapping("/register")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<UserDTO> registerUser(@RequestBody final UserRegistrationRequest request) {
    return ResponseEntity.ok(userService.registerUser(request));
  }

  @GetMapping("/{userId}")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<UserDTO> getUserById(@PathVariable final Long userId) {

    return ResponseEntity.ok(userService.findById(userId));
  }

  @GetMapping("/{userId}/permissions")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<UserPermissionDTO> getUserPermissions(@PathVariable final Long userId,
      @RequestParam(required = false) final Long appId) {

    if (ObjectUtils.isNotEmpty(appId)) {
      return ResponseEntity.ok(userService.findUserPermissions(userId, appId));
    } else {
      return ResponseEntity.ok(userService.findAllUserPermissions(userId));
    }
  }

  @PostMapping("/{userId}/app/{appId}")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<UserDTO> assignAppToUser(@PathVariable final Long userId,
      @PathVariable final Long appId) {

    return ResponseEntity.ok(userService.assignApplicationToUser(userId, appId));
  }

  @GetMapping
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<List<UserDTO>> getAllUsers() {
    final List<UserDTO> userDTOS = userService.findAll();

    return userDTOS.isEmpty()
        ? ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        : ResponseEntity.ok(userDTOS);
  }

  @PutMapping("/{userId}")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<UserDTO> updateUser(
      @PathVariable final Long userId,
      @RequestBody final UserRegistrationRequest userDetails) {
    final UserDTO updatedUser = userService.updateUser(userId, userDetails);
    return ResponseEntity.ok(updatedUser);
  }

  @PostMapping("/{userId}/status")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<Void> updateUserStatus(@PathVariable final Long userId,
      @RequestParam final boolean enabled) {
    userService.updateUserStatus(userId, enabled);
    return ResponseEntity.accepted().build();
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @PutMapping("/{userId}/permissions")
  public ResponseEntity<Void> updateUserPermissions(
      @PathVariable final Long userId,
      @RequestBody final List<PermissionUpdateRequest> permissionUpdates) {

    permissionService.updatePermissionsForUser(userId, permissionUpdates);
    return ResponseEntity.ok().build();
  }

}
