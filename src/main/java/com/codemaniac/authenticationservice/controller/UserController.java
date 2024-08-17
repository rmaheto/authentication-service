package com.codemaniac.authenticationservice.controller;

import com.codemaniac.authenticationservice.dto.UserDTO;
import com.codemaniac.authenticationservice.model.UserRegistrationRequest;
import com.codemaniac.authenticationservice.service.UserService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/register")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<UserDTO> registerUser(@RequestBody UserRegistrationRequest request) {
    UserDTO user = userService.registerUser(request);

    return ResponseEntity.ok(user);
  }

  @GetMapping("/{userId}")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
    Optional<UserDTO> user = userService.findById(userId);

    return user.map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(null));
  }

  @GetMapping("/{userId}/app/{appId}")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<UserDTO> getUserPermissionsByApp(@PathVariable Long userId,
      @PathVariable Long appId) {
    Optional<UserDTO> userDTO = userService.findUserPermissionsByApp(userId, appId);

    return userDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<List<UserDTO>> getAllUsers() {
    List<UserDTO> userDTOS = userService.findAll();

    return userDTOS.isEmpty()
        ? ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        : ResponseEntity.ok(userDTOS);
  }

  @PostMapping("/{userId}/status")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<Void> updateUserStatus(@PathVariable Long userId,
      @RequestParam boolean enabled) {
    userService.updateUserStatus(userId, enabled);
    return ResponseEntity.accepted().build();
  }

}
