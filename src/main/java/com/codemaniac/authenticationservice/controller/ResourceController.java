package com.codemaniac.authenticationservice.controller;

import com.codemaniac.authenticationservice.dto.ResourceDTO;
import com.codemaniac.authenticationservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
public class ResourceController {

  private final ResourceService resourceService;

  @PostMapping
  public ResponseEntity<Void> createResource(@RequestParam Long appId, @RequestParam String name) {
    resourceService.addResource(appId, name);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResourceDTO> getResourceById(@PathVariable Long id) {
    Optional<ResourceDTO> resourceDTO = resourceService.findById(id);
    return resourceDTO.map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  public ResponseEntity<Void> updateResource(@PathVariable Long id, @RequestBody ResourceDTO resourceDTO) {
    resourceService.updateResource(id, resourceDTO);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
    resourceService.deleteResource(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<List<ResourceDTO>> getAllResources() {
    return ResponseEntity.ok(resourceService.getAllResources());
  }
}
