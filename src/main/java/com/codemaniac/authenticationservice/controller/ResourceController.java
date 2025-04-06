package com.codemaniac.authenticationservice.controller;

import com.codemaniac.authenticationservice.dto.ResourceDTO;
import com.codemaniac.authenticationservice.service.ResourceService;
import java.util.Map;
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

  @PostMapping("/app/{appId}")
  public ResponseEntity<Void> createResource(@PathVariable final Long appId, @RequestBody final ResourceDTO resourceDTO) {
    resourceService.addResource(appId, resourceDTO);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResourceDTO> getResourceById(@PathVariable final Long id) {
    final Optional<ResourceDTO> resourceDTO = resourceService.findById(id);
    return resourceDTO.map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PatchMapping("/{id}")
  public ResponseEntity<Void> updateResource(@PathVariable final Long id,
      @RequestBody final Map<String, Object> updates) {
    resourceService.patchResource(id, updates);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteResource(@PathVariable final Long id) {
    resourceService.deleteResource(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<List<ResourceDTO>> getAllResources() {
    return ResponseEntity.ok(resourceService.getAllResources());
  }
}
