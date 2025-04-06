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
import com.codemaniac.authenticationservice.repository.PermissionRepository;
import com.codemaniac.authenticationservice.repository.ResourceRepository;
import com.codemaniac.authenticationservice.repository.UserRepository;
import jakarta.annotation.Nonnull;
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

  private final PermissionRepository permissionRepository;

  @Override
  public ApplicationDTO registerApplication(@Nonnull final String name, @Nonnull final String domain) {
    try {
      final Application application = new Application();
      application.setName(name);
      application.setDomain(domain);
      final Application savedApp = applicationRepository.save(application);
      log.info("registered app with name: {} and domain: {} ", name, domain);
      return ApplicationMapper.convertToDTO(savedApp);
    } catch (final Exception e) {
      log.warn("Failed to register app with name: {} with domain: {}", name, domain, e);
      return null;
    }
  }

  @Override
  public ApplicationDTO findByName(@Nonnull final String name) {
    final Application application = applicationRepository.findByName(name);
    return ApplicationMapper.convertToDTO(application);
  }

  @Override
  public boolean existsByDomain(@Nonnull final String domain) {
    return applicationRepository.findByDomain(domain) != null;
  }

  @Override
  public Optional<ApplicationDTO> findOne(@Nonnull final Long id) {
    return applicationRepository.findById(id).map(ApplicationMapper::convertToDTO);
  }

  @Override
  public Optional<Application> findById(@Nonnull final Long id) {
    return applicationRepository.findById(id);
  }

  @Override
  public List<ApplicationDTO> findAll() {
    return applicationRepository.findAll().stream()
        .map(ApplicationMapper::convertToDTO)
        .toList();
  }

  @Override
  public void updateApplication(@Nonnull final ApplicationDTO applicationDTO) {
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
  public ResourceDTO addResourceToApplication(@Nonnull final Long appId, @Nonnull final ResourceDTO resourceDTO) {
    final Application application = applicationRepository.findById(appId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Application not found with id: " + appId));

    Resource resource = new Resource();
    resource.setName(resourceDTO.getName());
    resource.setApplication(application);

    resource = resourceRepository.save(resource);

    assignDefaultPermissionsToUsers(application, resource);
    log.info("resource:{} added to app with name: {} successfully", application.getName(),
        resource);
    return ResourceMapper.toDTO(resource);
  }

  private void assignDefaultPermissionsToUsers(@Nonnull final Application application, @Nonnull final Resource resource) {

    final List<User> users = userRepository.findByApplicationsContaining(application);

    users.forEach(user -> {
      final Permission permission = new Permission();
      permission.setResource(resource);
      permission.setAction(new Action()); // Default values are false
      user.getPermissions().add(permission);
      permissionRepository.save(permission);
    });

    userRepository.saveAll(users);
  }
}
