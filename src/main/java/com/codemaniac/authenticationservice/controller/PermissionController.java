package com.codemaniac.authenticationservice.controller;

import com.codemaniac.authenticationservice.dto.PermissionDTO;
import com.codemaniac.authenticationservice.model.Action;
import com.codemaniac.authenticationservice.service.PermissionService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {

  private final PermissionService permissionService;

  @GetMapping("/{id}")
  public ResponseEntity<PermissionDTO> getPermissionById(@PathVariable Long id) {
    PermissionDTO permissionDTO = permissionService.getPermissionById(id);
    return ResponseEntity.ok(permissionDTO);
  }

  @GetMapping
  public ResponseEntity<List<PermissionDTO>> getPermissions(
      @RequestParam("userId") Optional<Long> userId,
      @RequestParam("appId") Optional<Long> appId) {
    List<PermissionDTO> permissions = permissionService.getPermissions(userId, appId);
    return ResponseEntity.ok(permissions);
  }

  @PostMapping
  public ResponseEntity<PermissionDTO> createPermission(@RequestBody PermissionDTO permissionDTO) {
    PermissionDTO createdPermission = permissionService.createPermission(permissionDTO);
    return ResponseEntity.ok(createdPermission);
  }

  @PutMapping("/{id}")
  public ResponseEntity<PermissionDTO> updatePermission(@PathVariable Long id,
      @RequestBody PermissionDTO permissionDTO) {
    PermissionDTO updatedPermission = permissionService.updatePermission(id, permissionDTO);
    return ResponseEntity.ok(updatedPermission);
  }

  @PostMapping("/{userId}/resources/{resourceId}/update-permissions")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<Void> updateUserPermissions(
      @PathVariable Long userId,
      @PathVariable Long resourceId,
      @RequestBody Action updatedAction) {
    permissionService.updatePermissionsForUser(userId, resourceId, updatedAction);
    return ResponseEntity.ok().build();
  }


  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
    permissionService.deletePermission(id);
    return ResponseEntity.noContent().build();
  }
}
