package com.codemaniac.authenticationservice.service;

import com.codemaniac.authenticationservice.dto.ApplicationDTO;
import com.codemaniac.authenticationservice.dto.ResourceDTO;
import com.codemaniac.authenticationservice.exception.ResourceNotFoundException;
import com.codemaniac.authenticationservice.model.Application;
import com.codemaniac.authenticationservice.model.Resource;
import com.codemaniac.authenticationservice.model.User;
import com.codemaniac.authenticationservice.repository.ApplicationRepository;
import com.codemaniac.authenticationservice.repository.PermissionRepository;
import com.codemaniac.authenticationservice.repository.ResourceRepository;
import com.codemaniac.authenticationservice.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceImplTest {

  @Mock private ApplicationRepository mockApplicationRepository;

  @Mock private ResourceRepository mockResourceRepository;

  @Mock private UserRepository mockUserRepository;

  @Mock private PermissionRepository mockPermissionRepository;

  @InjectMocks private ApplicationServiceImpl underTest;

  @Captor private ArgumentCaptor<Application> applicationCaptor;

  private static final String APP_NAME = "TestApp";

  private static final String DOMAIN = "test.com";

  private static final Long APP_ID = 1L;

  @Test
  void registerApplication_withValidData_shouldReturnDTO() {
    final Application app = new Application();
    app.setName(APP_NAME);
    app.setDomain(DOMAIN);

    when(mockApplicationRepository.save(any(Application.class))).thenReturn(app);

    final ApplicationDTO result = underTest.registerApplication(APP_NAME, DOMAIN);

    assertNotNull(result);
    assertEquals(APP_NAME, result.getName());
  }

  @Test
  void registerApplication_withException_shouldReturnNull() {

    when(mockApplicationRepository.save(any())).thenThrow(new RuntimeException("DB error"));

    final ApplicationDTO result = underTest.registerApplication(APP_NAME, DOMAIN);

    assertNull(result);
  }

  @Test
  void findByName_withExistingName_shouldReturnDTO() {
    final Application app = new Application();
    app.setName(APP_NAME);

    when(mockApplicationRepository.findByName(APP_NAME)).thenReturn(app);

    final ApplicationDTO result = underTest.findByName(APP_NAME);

    assertNotNull(result);
    assertEquals(APP_NAME, result.getName());
  }

  @Test
  void existsByDomain_withExistingDomain_shouldReturnTrue() {
    when(mockApplicationRepository.findByDomain(DOMAIN)).thenReturn(new Application());

    final boolean exists = underTest.existsByDomain(DOMAIN);

    assertTrue(exists);
  }

  @Test
  void existsByDomain_withNonExistingDomain_shouldReturnFalse() {
    final String domain = "notfound.com";
    when(mockApplicationRepository.findByDomain(domain)).thenReturn(null);

    final boolean exists = underTest.existsByDomain(domain);

    assertFalse(exists);
  }

  @Test
  void findOne_withValidId_shouldReturnDTOWrappedInOptional() {
    final Application app = new Application();
    app.setId(APP_ID);
    app.setName(APP_NAME);

    when(mockApplicationRepository.findById(APP_ID)).thenReturn(Optional.of(app));

    final Optional<ApplicationDTO> result = underTest.findOne(APP_ID);

    assertTrue(result.isPresent());
    assertEquals(APP_NAME, result.get().getName());
  }

  @Test
  void findById_withValidId_shouldReturnOptionalApplication() {
    final Application app = new Application();
    app.setId(APP_ID);

    when(mockApplicationRepository.findById(APP_ID)).thenReturn(Optional.of(app));

    final Optional<Application> result = underTest.findById(APP_ID);

    assertTrue(result.isPresent());
    assertEquals(APP_ID, result.get().getId());
  }

  @Test
  void findAll_withExistingApps_shouldReturnListOfDTOs() {
    final List<Application> apps = List.of(new Application(), new Application());

    when(mockApplicationRepository.findAll()).thenReturn(apps);

    final List<ApplicationDTO> result = underTest.findAll();

    assertEquals(2, result.size());
  }

  @Test
  void updateApplication_withValidDTO_shouldUpdateEntity() {
    final Application app = new Application();
    app.setId(APP_ID);
    app.setName("OldName");
    app.setDomain("old.com");

    final ApplicationDTO dto = new ApplicationDTO();
    dto.setId(APP_ID);
    dto.setName(APP_NAME);
    dto.setDomain(DOMAIN);

    when(mockApplicationRepository.findById(APP_ID)).thenReturn(Optional.of(app));

    underTest.updateApplication(dto);

    verify(mockApplicationRepository).save(applicationCaptor.capture());
    final Application application = applicationCaptor.getValue();

    assertEquals(APP_NAME, application.getName());
    assertEquals(DOMAIN, application.getDomain());
  }

  @Test
  void updateApplication_withNonExistingApp_shouldNotSave() {
    final ApplicationDTO dto = new ApplicationDTO();
    dto.setId(999L);
    dto.setName("NewName");
    dto.setDomain("new.com");

    when(mockApplicationRepository.findById(999L)).thenReturn(Optional.empty());

    underTest.updateApplication(dto);

    verify(mockApplicationRepository, never()).save(any());
  }

  @Test
  void updateApplication_withBlankName_shouldNotUpdateName() {
    final String updatedDomain = "updated.com";
    final Application app = new Application();
    app.setId(APP_ID);
    app.setName(APP_NAME);

    final ApplicationDTO dto = new ApplicationDTO();
    dto.setId(APP_ID);
    dto.setName(StringUtils.EMPTY);
    dto.setDomain(updatedDomain);

    when(mockApplicationRepository.findById(APP_ID)).thenReturn(Optional.of(app));

    underTest.updateApplication(dto);

    verify(mockApplicationRepository).save(applicationCaptor.capture());
    final Application application = applicationCaptor.getValue();

    assertEquals(APP_NAME, application.getName());
    assertEquals(updatedDomain, app.getDomain());
  }

  @Test
  void updateApplication_withBlankDomain_shouldNotUpdateDomain() {

    final Application app = new Application();
    app.setId(APP_ID);
    app.setName(APP_NAME);
    app.setDomain(DOMAIN);

    final ApplicationDTO dto = new ApplicationDTO();
    dto.setId(APP_ID);
    dto.setName(APP_NAME);
    dto.setDomain(StringUtils.EMPTY);

    when(mockApplicationRepository.findById(APP_ID)).thenReturn(Optional.of(app));

    underTest.updateApplication(dto);

    verify(mockApplicationRepository).save(applicationCaptor.capture());
    final Application application = applicationCaptor.getValue();

    assertEquals(APP_NAME, application.getName());
    assertEquals(DOMAIN, application.getDomain());
  }

  @Test
  void addResourceToApplication_withValidInput_shouldReturnResourceDTO() {

    final Application app = new Application();
    app.setId(APP_ID);
    app.setName(APP_NAME);

    final ResourceDTO resourceDTO = new ResourceDTO();
    resourceDTO.setName("Resource1");

    final Resource savedResource = new Resource();
    savedResource.setName("Resource1");
    savedResource.setApplication(app);

    when(mockApplicationRepository.findById(APP_ID)).thenReturn(Optional.of(app));
    when(mockResourceRepository.save(any(Resource.class))).thenReturn(savedResource);
    when(mockUserRepository.findByApplicationsContaining(app)).thenReturn(List.of(new User()));

    final ResourceDTO result = underTest.addResourceToApplication(APP_ID, resourceDTO);

    assertNotNull(result);
    assertEquals("Resource1", result.getName());
    verify(mockResourceRepository).save(any(Resource.class));
    verify(mockUserRepository).saveAll(any());
  }

  @Test
  void addResourceToApplication_withInvalidAppId_shouldThrowException() {
    final ResourceDTO resourceDTO = new ResourceDTO();
    resourceDTO.setName("R");

    when(mockApplicationRepository.findById(APP_ID)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> underTest.addResourceToApplication(APP_ID, resourceDTO));
  }
}
