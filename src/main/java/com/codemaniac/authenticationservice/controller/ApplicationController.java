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
      @RequestBody ApplicationDTO applicationDTO) {
    ApplicationDTO registeredApp = applicationService.registerApplication(applicationDTO.getName(),
        applicationDTO.getDomain());
    return ResponseEntity.ok(registeredApp);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApplicationDTO> getApplicationById(@PathVariable Long id) {
    ApplicationDTO applicationDTO = applicationService.findOne(id)
        .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));
    return ResponseEntity.ok(applicationDTO);
  }

  @GetMapping
  public ResponseEntity<List<ApplicationDTO>> getAllApplications() {
    return ResponseEntity.ok(applicationService.findAll());
  }

  @PutMapping("/{id}")
  public ResponseEntity<Void> updateApplication(@PathVariable Long id,
      @RequestBody ApplicationDTO applicationDTO) {
    applicationDTO.setId(id); // Ensure the ID in the DTO is set to the path variable ID
    applicationService.updateApplication(applicationDTO);
    return ResponseEntity.noContent()
        .build();
  }

  @PostMapping("/{appId}/resources")
  public ResponseEntity<ResourceDTO> addResourceToApplication(@PathVariable Long appId,
      @RequestBody ResourceDTO resourceDTO) {
    ResourceDTO createdResource = applicationService.addResourceToApplication(appId, resourceDTO);
    return ResponseEntity.ok(createdResource);
  }
}
