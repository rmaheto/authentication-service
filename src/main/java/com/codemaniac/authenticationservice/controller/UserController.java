package com.codemaniac.authenticationservice.controller;

import com.codemaniac.authenticationservice.dto.UserDTO;
import com.codemaniac.authenticationservice.exception.ResourceNotFoundException;
import com.codemaniac.authenticationservice.model.AuthenticationRequest;
import com.codemaniac.authenticationservice.model.User;
import com.codemaniac.authenticationservice.service.UserService;
import com.codemaniac.authenticationservice.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/register")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<UserDTO> registerUser(@RequestBody AuthenticationRequest request) {
    UserDTO user = userService.registerUser(request);
    return ResponseEntity.ok(user);
  }

  @PostMapping("/{userId}/assign-permission")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<UserDTO> assignPermissionsToUser(@PathVariable Long userId, @RequestBody List<Long> permissionIds) {
    Optional<UserDTO> userOptional = userService.findById(userId);
    if (userOptional.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    UserDTO user = userOptional.get();
    userService.assignPermissionsToUser(user, new HashSet<>());
    return ResponseEntity.ok(user);
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
    UserDTO user = userService.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    return ResponseEntity.ok(user);
  }

  @PostMapping("/{userId}/status")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<Void> updateUserStatus(@PathVariable Long userId, @RequestParam boolean enabled) {
    userService.updateUserStatus(userId, enabled);
    return ResponseEntity.accepted().build();
  }

  @GetMapping
  public ResponseEntity<List<UserDTO>> getAllUsers() {
    return ResponseEntity.ok(userService.findAll());
  }
}
