package com.codemaniac.authenticationservice.mapper;

import com.codemaniac.authenticationservice.dto.ResourceDTO;
import com.codemaniac.authenticationservice.model.Resource;

public class ResourceMapper {

  private ResourceMapper() {

  }

  public static ResourceDTO toDTO(Resource resource) {
    ResourceDTO resourceDTO = new ResourceDTO();
    resourceDTO.setId(resource.getId());
    resourceDTO.setName(resource.getName());
    resourceDTO.setApplication(ApplicationMapper.toDTO(resource.getApplication()));
    return resourceDTO;
  }

  public static Resource toEntity(ResourceDTO resourceDTO) {
    Resource resource = new Resource();
    resource.setId(resourceDTO.getId());
    resource.setName(resourceDTO.getName());

    return resource;
  }

  private Resource convertToEntity(ResourceDTO resourceDTO) {
    Resource resource = new Resource();
    resource.setId(resourceDTO.getId());
    resource.setName(resourceDTO.getName());
    resource.setApplication(ApplicationMapper.toEntity(resourceDTO.getApplication()));
    return resource;
  }
}
