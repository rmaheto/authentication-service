package com.codemaniac.authenticationservice.service;

import com.codemaniac.authenticationservice.dto.ResourceDTO;
import com.codemaniac.authenticationservice.model.Resource;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ResourceService {

  Optional<ResourceDTO> findById(@Nonnull Long id);

  void addResource(@Nonnull Long appId, @Nonnull ResourceDTO resourceDTO);

  void patchResource(@Nonnull Long id, @Nonnull Map<String, Object> updates);

  void deleteResource(@Nonnull Long id);

  List<ResourceDTO> getAllResources();
}
