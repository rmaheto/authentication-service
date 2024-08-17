package com.codemaniac.authenticationservice.service;

import com.codemaniac.authenticationservice.dto.ApplicationDTO;
import com.codemaniac.authenticationservice.dto.ResourceDTO;
import com.codemaniac.authenticationservice.model.Application;
import com.codemaniac.authenticationservice.model.Resource;

import java.util.List;
import java.util.Optional;

public interface ApplicationService {

  ApplicationDTO registerApplication(String name, String domain);
  ApplicationDTO findByName(String name);
  boolean existsByDomain(String domain);
  Optional<ApplicationDTO> findById(Long id);
  List<ApplicationDTO> findAll();
  ResourceDTO addResourceToApplication(ApplicationDTO application, String resourceName);
  void addResourceToApplication(ApplicationDTO application, ResourceDTO resource);
  void updateApplication(ApplicationDTO applicationDTO);
}
