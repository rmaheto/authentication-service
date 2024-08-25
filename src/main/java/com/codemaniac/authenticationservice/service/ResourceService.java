package com.codemaniac.authenticationservice.service;

import com.codemaniac.authenticationservice.dto.ResourceDTO;
import com.codemaniac.authenticationservice.model.Resource;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ResourceService {

  Optional<ResourceDTO> findById(Long id);

  void addResource(Long appId, ResourceDTO resourceDTO);

  void patchResource(Long id, Map<String, Object> updates);

  void deleteResource(Long id);

  List<ResourceDTO> getAllResources();
}
