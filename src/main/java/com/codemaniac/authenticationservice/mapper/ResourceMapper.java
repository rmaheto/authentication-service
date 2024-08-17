package com.codemaniac.authenticationservice.mapper;

import com.codemaniac.authenticationservice.dto.ResourceDTO;
import com.codemaniac.authenticationservice.model.Resource;

public class ResourceMapper {
  public static ResourceDTO toDTO(Resource resource) {
    ResourceDTO resourceDTO = new ResourceDTO();
    resourceDTO.setId(resource.getId());
    resourceDTO.setName(resource.getName());
    resourceDTO.setApplication(ApplicationMapper.toDTO(resource.getApplication()));
    return resourceDTO;
  }
}
