package com.codemaniac.authenticationservice.service;

import com.codemaniac.authenticationservice.dto.ResourceDTO;
import com.codemaniac.authenticationservice.mapper.ResourceMapper;
import com.codemaniac.authenticationservice.mapper.UserMapper;
import com.codemaniac.authenticationservice.model.Resource;
import com.codemaniac.authenticationservice.repository.ResourceRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

  private final ResourceRepository resourceRepository;
  private final ApplicationService applicationService;

  @Override
  public Optional<ResourceDTO> findById(Long id) {
    return resourceRepository.findById(id)
        .map(ResourceMapper::toDTO);
  }

  @Override
  public void addResource(Long appId, ResourceDTO resourceDTO) {
    applicationService.addResourceToApplication(appId, resourceDTO);
  }

  @Override
  public void updateResource(Long id, ResourceDTO resourceDTO) {
    Optional<Resource> resourceOpt = resourceRepository.findById(id);
    if (resourceOpt.isPresent()) {
      Resource resource = resourceOpt.get();
      if (StringUtils.isNotBlank(resourceDTO.getName())) {
        resource.setName(resourceDTO.getName());
      }
//      if (resourceDTO.getAppId() != null) {
//        applicationRepository.findById(resourceDTO.getAppId()).ifPresent(resource::setApplication);
//      }
      resourceRepository.save(resource);
    }
  }

  @Override
  public void deleteResource(Long id) {
    resourceRepository.deleteById(id);
  }

  @Override
  public List<ResourceDTO> getAllResources() {
    return resourceRepository.findAll().stream()
        .map(ResourceMapper::toDTO)
        .toList();
  }
}
