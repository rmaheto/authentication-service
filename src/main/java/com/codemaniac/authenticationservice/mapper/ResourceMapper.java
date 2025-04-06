package com.codemaniac.authenticationservice.mapper;

import com.codemaniac.authenticationservice.dto.ResourceDTO;
import com.codemaniac.authenticationservice.model.Resource;
import jakarta.annotation.Nonnull;

public class ResourceMapper {

  private ResourceMapper() {

  }

  public static ResourceDTO toDTO(@Nonnull final Resource resource) {
    ResourceDTO resourceDTO = null;

      resourceDTO = new ResourceDTO();
      resourceDTO.setId(resource.getId());
      resourceDTO.setName(resource.getName());
      resourceDTO.setApplication(ApplicationMapper.toDTO(resource.getApplication()));

    return resourceDTO;
  }

  public static Resource toEntity(@Nonnull final ResourceDTO resourceDTO) {
    final Resource resource = new Resource();
    resource.setId(resourceDTO.getId());
    resource.setName(resourceDTO.getName());

    return resource;
  }

  private Resource convertToEntity(@Nonnull final ResourceDTO resourceDTO) {
    Resource resource = new Resource();
    resource.setId(resourceDTO.getId());
    resource.setName(resourceDTO.getName());
    resource.setApplication(ApplicationMapper.toEntity(resourceDTO.getApplication()));
    return resource;
  }
}
