package com.codemaniac.authenticationservice.service;

import com.codemaniac.authenticationservice.dto.ApplicationDTO;
import com.codemaniac.authenticationservice.dto.ApplicationMapper;
import com.codemaniac.authenticationservice.dto.ResourceDTO;
import com.codemaniac.authenticationservice.exception.ResourceNotFoundException;
import com.codemaniac.authenticationservice.mapper.ResourceMapper;
import com.codemaniac.authenticationservice.model.Action;
import com.codemaniac.authenticationservice.model.Application;
import com.codemaniac.authenticationservice.model.Permission;
import com.codemaniac.authenticationservice.model.Resource;
import com.codemaniac.authenticationservice.model.User;
import com.codemaniac.authenticationservice.repository.ApplicationRepository;
import com.codemaniac.authenticationservice.repository.ResourceRepository;
import com.codemaniac.authenticationservice.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

  private final ApplicationRepository applicationRepository;

  private final ResourceRepository resourceRepository;

  private final UserRepository userRepository;

  @Override
  public ApplicationDTO registerApplication(String name, String domain) {
    try {
      Application application = new Application();
      application.setName(name);
      application.setDomain(domain);
      Application savedApp = applicationRepository.save(application);
      log.info("registered app with name: {} and domain: {} ", name, domain);
      return ApplicationMapper.convertToDTO(savedApp);
    } catch (Exception e) {
      log.warn("Failed to register app with name: {} with domain: {}", name, domain, e);
      return null;
    }
  }

  @Override
  public ApplicationDTO findByName(String name) {
    Application application = applicationRepository.findByName(name);
    return ApplicationMapper.convertToDTO(application);
  }

  @Override
  public boolean existsByDomain(String domain) {
    return applicationRepository.findByDomain(domain) != null;
  }

  @Override
  public Optional<ApplicationDTO> findById(Long id) {
    return applicationRepository.findById(id).map(ApplicationMapper::convertToDTO);
  }

  @Override
  public List<ApplicationDTO> findAll() {
    return applicationRepository.findAll().stream()
        .map(ApplicationMapper::convertToDTO)
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
  @Transactional
  public ResourceDTO addResourceToApplication(Long appId, ResourceDTO resourceDTO) {
    Application application = applicationRepository.findById(appId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Application not found with id: " + appId));

    // Create the new resource
    Resource resource = new Resource();
    resource.setName(resourceDTO.getName());
    resource.setApplication(application);

    resource = resourceRepository.save(resource);

    // Assign default permissions to all users assigned to the application
    assignDefaultPermissionsToUsers(application, resource);
    log.info("resource:{} added to app with name: {} successfully", application.getName(),
        resource);
    return ResourceMapper.toDTO(resource);
  }

  private void assignDefaultPermissionsToUsers(Application application, Resource resource) {
    // Fetch all users assigned to the application
    List<User> users = userRepository.findByApplicationsContaining(application);

    users.forEach(user -> {
      // Create a new permission for the resource with default values
      Permission permission = new Permission();
      permission.setResource(resource);
      permission.setAction(new Action()); // Default values are false

      // Add the permission to the user's permissions
      user.getPermissions().add(permission);
    });

    // Save the users with the new permissions
    userRepository.saveAll(users);
  }
}
