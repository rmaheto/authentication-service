package com.codemaniac.authenticationservice.service;

import com.codemaniac.authenticationservice.dto.ApplicationDTO;
import com.codemaniac.authenticationservice.dto.ResourceDTO;
import com.codemaniac.authenticationservice.model.Application;
import com.codemaniac.authenticationservice.model.Resource;
import com.codemaniac.authenticationservice.model.audit.Audit;
import com.codemaniac.authenticationservice.repository.ApplicationRepository;
import com.codemaniac.authenticationservice.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService{

    private final ResourceRepository resourceRepository;
    private final ApplicationRepository applicationRepository;

  @Override
  public Optional<ResourceDTO> findById(Long id) {
    return resourceRepository.findById(id)
            .map(this::convertToDTO);
  }

  @Override
  public void addResource(Long appId, String name) {
    Optional<Application> application = applicationRepository.findById(appId);
    application.ifPresent(app -> {
      Audit audit = new Audit(Audit.PROGRAM, Audit.SYSTEM);
      Resource resource = new Resource();
      resource.setName(name);
      resource.setApplication(app);
      resource.setAudit(audit);
      resourceRepository.save(resource);
    });
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
            .map(this::convertToDTO)
            .toList();
  }

  private ResourceDTO convertToDTO(Resource resource) {
    return new ResourceDTO(resource.getId(), resource.getName(),
            new ApplicationDTO(resource.getApplication().getId(), resource.getApplication().getName(),
                    resource.getApplication().getDomain()));
  }
}
