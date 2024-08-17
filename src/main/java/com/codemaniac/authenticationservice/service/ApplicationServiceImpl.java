package com.codemaniac.authenticationservice.service;

import com.codemaniac.authenticationservice.dto.ApplicationDTO;
import com.codemaniac.authenticationservice.dto.ResourceDTO;
import com.codemaniac.authenticationservice.model.Application;
import com.codemaniac.authenticationservice.model.Resource;
import com.codemaniac.authenticationservice.repository.ApplicationRepository;
import com.codemaniac.authenticationservice.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

  private final ApplicationRepository applicationRepository;

  private final ResourceRepository resourceRepository;

  @Override
  public ApplicationDTO registerApplication(String name, String domain) {
    try {
      Application application = new Application();
      application.setName(name);
      application.setDomain(domain);
      Application savedApp = applicationRepository.save(application);
      log.info("registered app with name: {} and domain: {} ", name, domain);
      return convertToDTO(savedApp);
    } catch (Exception e) {
      log.warn("Failed to register app with name: {} with domain: {}", name, domain, e);
      return null;
    }
  }

  @Override
  public ApplicationDTO findByName(String name) {
    Application application = applicationRepository.findByName(name);
    return convertToDTO(application);
  }

  @Override
  public boolean existsByDomain(String domain) {
    return applicationRepository.findByDomain(domain) != null;
  }

  @Override
  public Optional<ApplicationDTO> findById(Long id) {
    return applicationRepository.findById(id).map(this::convertToDTO);
  }

  @Override
  public List<ApplicationDTO> findAll() {
    return applicationRepository.findAll().stream()
            .map(this::convertToDTO)
            .toList();
  }

  @Override
  public void updateApplication(ApplicationDTO applicationDTO) {
    applicationRepository.findById(applicationDTO.getId())
            .ifPresent(application -> {
              if (StringUtils.isNotBlank(applicationDTO.getName())) {
                application.setName(applicationDTO.getName());
              }
              if (StringUtils.isNotBlank(applicationDTO.getDomain())) {
                application.setDomain(applicationDTO.getDomain());
              }
              applicationRepository.save(application);
            });
  }

  @Override
  public ResourceDTO addResourceToApplication(ApplicationDTO applicationDTO, String resourceName) {
    Resource resource = new Resource();
    Application application = convertToEntity(applicationDTO);
    try {
      resource.setName(resourceName);
      resource.setApplication(application);
      Resource savedResource = resourceRepository.save(resource);
      log.info("resource:{} added to app with name: {} successfully", application.getName(), resourceName);
      return convertToDTO(savedResource);
    } catch (Exception e) {
      log.warn("Failed to add resource:{} to app with name: {}", application.getName(), resourceName, e);
      return null;
    }
  }

  @Override
  public void addResourceToApplication(ApplicationDTO applicationDTO, ResourceDTO resourceDTO) {
    Application application = convertToEntity(applicationDTO);
    Resource resource = convertToEntity(resourceDTO);
    application.getResources().add(resource);
    applicationRepository.save(application);
  }

  private ApplicationDTO convertToDTO(Application application) {
    ApplicationDTO applicationDTO = new ApplicationDTO();
    applicationDTO.setId(application.getId());
    applicationDTO.setName(application.getName());
    applicationDTO.setDomain(application.getDomain());
    return applicationDTO;
  }

  private ResourceDTO convertToDTO(Resource resource) {
    ResourceDTO resourceDTO = new ResourceDTO();
    resourceDTO.setId(resource.getId());
    resourceDTO.setName(resource.getName());
    resourceDTO.setApplication(convertToDTO(resource.getApplication()));
    return resourceDTO;
  }

  private Application convertToEntity(ApplicationDTO applicationDTO) {
    Application application = new Application();
    application.setId(applicationDTO.getId());
    application.setName(applicationDTO.getName());
    application.setDomain(applicationDTO.getDomain());
    return application;
  }

  private Resource convertToEntity(ResourceDTO resourceDTO) {
    Resource resource = new Resource();
    resource.setId(resourceDTO.getId());
    resource.setName(resourceDTO.getName());
    resource.setApplication(convertToEntity(resourceDTO.getApplication()));
    return resource;
  }
}
