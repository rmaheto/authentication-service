package com.codemaniac.authenticationservice.controller;

import com.codemaniac.authenticationservice.dto.ApplicationDTO;
import com.codemaniac.authenticationservice.dto.ResourceDTO;
import com.codemaniac.authenticationservice.exception.ResourceNotFoundException;
import com.codemaniac.authenticationservice.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("applications")
@RequiredArgsConstructor
public class ApplicationController {

  private final ApplicationService applicationService;

  @PostMapping
  public ResponseEntity<ApplicationDTO> registerApplication(
      @RequestBody final ApplicationDTO applicationDTO) {
    final ApplicationDTO registeredApp = applicationService.registerApplication(applicationDTO.getName(),
        applicationDTO.getDomain());
    return ResponseEntity.ok(registeredApp);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApplicationDTO> getApplicationById(@PathVariable Long id) {
    final ApplicationDTO applicationDTO = applicationService.findOne(id)
        .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));
    return ResponseEntity.ok(applicationDTO);
  }

  @GetMapping
  public ResponseEntity<List<ApplicationDTO>> getAllApplications() {
    return ResponseEntity.ok(applicationService.findAll());
  }

  @PutMapping("/{id}")
  public ResponseEntity<Void> updateApplication(@PathVariable Long id,
      @RequestBody final ApplicationDTO applicationDTO) {
    applicationDTO.setId(id); // Ensure the ID in the DTO is set to the path variable ID
    applicationService.updateApplication(applicationDTO);
    return ResponseEntity.noContent()
        .build();
  }

  @PostMapping("/{appId}/resources")
  public ResponseEntity<ResourceDTO> addResourceToApplication(@PathVariable final Long appId,
      @RequestBody final ResourceDTO resourceDTO) {
    final ResourceDTO createdResource = applicationService.addResourceToApplication(appId, resourceDTO);
    return ResponseEntity.ok(createdResource);
  }
}
