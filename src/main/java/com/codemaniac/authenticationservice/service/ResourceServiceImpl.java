package com.codemaniac.authenticationservice.service;

import com.codemaniac.authenticationservice.dto.ResourceDTO;
import com.codemaniac.authenticationservice.exception.ResourceNotFoundException;
import com.codemaniac.authenticationservice.mapper.ResourceMapper;
import com.codemaniac.authenticationservice.model.Application;
import com.codemaniac.authenticationservice.model.Resource;
import com.codemaniac.authenticationservice.repository.ResourceRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
  @Transactional
  public void patchResource(Long id, Map<String, Object> updates) {
    Resource resource = resourceRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + id));

    if (updates.containsKey("name")) {
      String name = (String) updates.get("name");
      if (StringUtils.isNotBlank(name)) {
        resource.setName(name);
      }
    }

    if (updates.containsKey("appId")) {
      Long appId = Long.valueOf(updates.get("appId").toString());
      Application application = applicationService.findById(appId)
          .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + appId));
      resource.setApplication(application);
    }

    resourceRepository.save(resource);
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
