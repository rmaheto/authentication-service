package com.codemaniac.authenticationservice.controller;

import com.codemaniac.authenticationservice.dto.PermissionDTO;
import com.codemaniac.authenticationservice.exception.ResourceNotFoundException;
import com.codemaniac.authenticationservice.model.Action;
import com.codemaniac.authenticationservice.model.Permission;
import com.codemaniac.authenticationservice.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
  public ResponseEntity<PermissionDTO> updatePermission(@PathVariable Long id, @RequestBody PermissionDTO permissionDTO) {
    PermissionDTO updatedPermission = permissionService.updatePermission(id, permissionDTO);
    return ResponseEntity.ok(updatedPermission);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
    permissionService.deletePermission(id);
    return ResponseEntity.noContent().build();
  }
}
