package com.codemaniac.authenticationservice.service;

import com.codemaniac.authenticationservice.dto.PermissionDTO;
import com.codemaniac.authenticationservice.exception.ResourceNotFoundException;
import com.codemaniac.authenticationservice.mapper.PermissionMapper;
import com.codemaniac.authenticationservice.mapper.ResourceMapper;
import com.codemaniac.authenticationservice.model.Action;
import com.codemaniac.authenticationservice.model.Application;
import com.codemaniac.authenticationservice.model.Permission;
import com.codemaniac.authenticationservice.model.PermissionUpdateRequest;
import com.codemaniac.authenticationservice.model.Resource;
import com.codemaniac.authenticationservice.model.User;
import com.codemaniac.authenticationservice.repository.ApplicationRepository;
import com.codemaniac.authenticationservice.repository.PermissionRepository;
import com.codemaniac.authenticationservice.repository.ResourceRepository;
import com.codemaniac.authenticationservice.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PermissionServiceImplTest {

  @Mock private PermissionRepository mockPermissionRepository;

  @Mock private UserRepository mockUserRepository;

  @Mock private ResourceRepository mockResourceRepository;

  @Mock private ApplicationRepository mockApplicationRepository;

  @InjectMocks private PermissionServiceImpl underTest;

  private static final Long PERMISSION_ID = 1L;

  private static final Long USER_ID = 100L;

  private static final Long APP_ID = 200L;

  private static final Long RESOURCE_ID = 300L;

  @Test
  void getPermissionById_withExistingId_shouldReturnDTO() {
    try (final MockedStatic<PermissionMapper> permissionMapperMockedStatic =
        mockStatic(PermissionMapper.class)) {

      permissionMapperMockedStatic
          .when(() -> PermissionMapper.toDTO(any()))
          .thenReturn(new PermissionDTO());
      when(mockPermissionRepository.findById(PERMISSION_ID))
          .thenReturn(Optional.of(buildPermission(PERMISSION_ID, APP_ID, RESOURCE_ID)));

      final PermissionDTO result = underTest.getPermissionById(PERMISSION_ID);

      assertNotNull(result);
    }
  }

  @Test
  void getPermissionById_withMissingId_shouldThrowException() {
    when(mockPermissionRepository.findById(PERMISSION_ID)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> underTest.getPermissionById(PERMISSION_ID));
  }

  @Test
  void createPermission_withValidDTO_shouldReturnSavedDTO() {

    try (final MockedStatic<PermissionMapper> mockedPermissionMapper =
        mockStatic(PermissionMapper.class)) {

      final PermissionDTO dto = new PermissionDTO();
      final Permission permission = buildPermission(PERMISSION_ID, APP_ID, RESOURCE_ID);

      mockedPermissionMapper.when(() -> PermissionMapper.toEntity(any())).thenReturn(permission);
      mockedPermissionMapper
          .when(() -> PermissionMapper.toDTO(any()))
          .thenReturn(new PermissionDTO());
      when(mockPermissionRepository.save(any())).thenReturn(permission);

      final PermissionDTO result = underTest.createPermission(dto);

      assertNotNull(result);
    }
  }

  @Test
  void updatePermission_withExistingId_shouldUpdateAndReturnDTO() {

    try (final MockedStatic<ResourceMapper> resourceMapperMockedStatic =
            mockStatic(ResourceMapper.class);
        final MockedStatic<PermissionMapper> permissionMapperMockedStatic =
            mockStatic(PermissionMapper.class)) {

      final Permission permission = buildPermission(PERMISSION_ID, APP_ID, RESOURCE_ID);

      final PermissionDTO dto = new PermissionDTO();
      dto.setAction(new Action());

      final Resource mockedResource = new Resource();
      resourceMapperMockedStatic
          .when(() -> ResourceMapper.toEntity(any()))
          .thenReturn(mockedResource);
      permissionMapperMockedStatic
          .when(() -> PermissionMapper.toDTO(any()))
          .thenReturn(new PermissionDTO());

      when(mockPermissionRepository.findById(PERMISSION_ID)).thenReturn(Optional.of(permission));
      when(mockPermissionRepository.save(permission)).thenReturn(permission);

      final PermissionDTO result = underTest.updatePermission(PERMISSION_ID, dto);

      assertNotNull(result);
    }
  }

  @Test
  void updatePermission_withInvalidId_shouldThrowException() {
    final PermissionDTO dto = new PermissionDTO();

    when(mockPermissionRepository.findById(PERMISSION_ID)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> underTest.updatePermission(PERMISSION_ID, dto));
  }

  @Test
  void deletePermission_withValidId_shouldDelete() {
    final Permission permission = new Permission();
    when(mockPermissionRepository.findById(PERMISSION_ID)).thenReturn(Optional.of(permission));

    underTest.deletePermission(PERMISSION_ID);

    verify(mockPermissionRepository).delete(permission);
  }

  @Test
  void updatePermissionsForUser_withValidUpdates_shouldUpdateAndSaveUser() {
    final Long userId = 1L;
    final Long resourceId = 2L;
    final Action updatedAction = new Action();
    updatedAction.setCreate(true);

    final PermissionUpdateRequest request = new PermissionUpdateRequest();
    request.setResourceId(resourceId);
    request.setUpdatedAction(updatedAction);

    final Resource resource = new Resource();
    resource.setId(resourceId);

    final Permission existingPermission = new Permission();
    existingPermission.setResource(resource);
    existingPermission.setAction(new Action());

    final User user = new User();
    user.setId(userId);
    user.setPermissions(new java.util.HashSet<>(List.of(existingPermission)));

    when(mockUserRepository.findById(userId)).thenReturn(Optional.of(user));
    when(mockResourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));

    underTest.updatePermissionsForUser(userId, List.of(request));

    verify(mockUserRepository).save(user);
    assertEquals(updatedAction, existingPermission.getAction());
  }

  @Test
  void updatePermissionsForUser_withMissingPermission_shouldCreateNewPermission() {
    final Long userId = 1L;
    final Long resourceId = 2L;
    final Action updatedAction = new Action();
    updatedAction.setDelete(true);

    final PermissionUpdateRequest request = new PermissionUpdateRequest();
    request.setResourceId(resourceId);
    request.setUpdatedAction(updatedAction);

    final Resource resource = new Resource();
    resource.setId(resourceId);

    final User user = new User();
    user.setId(userId);
    user.setPermissions(new java.util.HashSet<>()); // no permissions yet

    when(mockUserRepository.findById(userId)).thenReturn(Optional.of(user));
    when(mockResourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));

    underTest.updatePermissionsForUser(userId, List.of(request));

    verify(mockUserRepository).save(user);

    assertEquals(1, user.getPermissions().size());
    final Permission newPermission = user.getPermissions().iterator().next();
    assertEquals(resource, newPermission.getResource());
    assertEquals(updatedAction, newPermission.getAction());
  }

  @Test
  void updatePermissionsForUser_withInvalidUser_shouldThrow() {
    final Long userId = 1L;

    final List<PermissionUpdateRequest> requests = List.of();

    when(mockUserRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> underTest.updatePermissionsForUser(userId, requests));
  }

  @Test
  void updatePermissionsForUser_withValidRequests_shouldSaveUser() {
    final User user = new User();
    user.setPermissions(new java.util.HashSet<>());

    final Resource resource = new Resource();
    resource.setId(RESOURCE_ID);

    final PermissionUpdateRequest request = new PermissionUpdateRequest();
    request.setResourceId(RESOURCE_ID);
    request.setUpdatedAction(new Action());

    when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(mockResourceRepository.findById(anyLong())).thenReturn(Optional.of(resource));
    when(mockPermissionRepository.findByUserAndResource(any(), any()))
        .thenReturn(Optional.of(buildPermission(PERMISSION_ID, APP_ID, RESOURCE_ID)));

    underTest.updatePermissionsForUser(USER_ID, RESOURCE_ID, new Action());

    verify(mockPermissionRepository).save(any());
  }

  @Test
  void updatePermissionsForUser_withInvalidUser_shouldThrowException() {
    final Long userId = 1L;
    final Action action = new Action();

    final List<PermissionUpdateRequest> requests = List.of();

    when(mockUserRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> underTest.updatePermissionsForUser(userId, 2L, action));

    assertThrows(
        ResourceNotFoundException.class,
        () -> underTest.updatePermissionsForUser(userId, requests));
  }

  @Test
  void updatePermissionsForUser_withInvalidResource_shouldThrow() {
    final User user = new User();

    final PermissionUpdateRequest request = new PermissionUpdateRequest();
    request.setResourceId(RESOURCE_ID);
    request.setUpdatedAction(new Action());

    final List<PermissionUpdateRequest> requestList = List.of(request);

    final Action updatedAction = new Action();

    when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(mockResourceRepository.findById(RESOURCE_ID)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> underTest.updatePermissionsForUser(USER_ID, RESOURCE_ID, updatedAction));
    assertThrows(
        ResourceNotFoundException.class,
        () -> underTest.updatePermissionsForUser(USER_ID, requestList));
  }

  @Test
  void deletePermission_withInvalidId_shouldThrow() {
    when(mockPermissionRepository.findById(PERMISSION_ID)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> underTest.deletePermission(PERMISSION_ID));
  }

  @Test
  void getPermissions_withUserAndApp_shouldReturnFilteredPermissions() {
    final Application app = new Application();
    app.setId(APP_ID);

    final Resource resource = new Resource();
    resource.setApplication(app);

    final Permission permission = new Permission();
    permission.setResource(resource);

    final User user = new User();
    user.setPermissions(Set.of(permission));

    when(mockApplicationRepository.findById(APP_ID)).thenReturn(Optional.of(app));
    when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.of(user));

    final List<PermissionDTO> result =
        underTest.getPermissions(Optional.of(USER_ID), Optional.of(APP_ID));

    assertEquals(1, result.size());
  }

  @Test
  void getPermissions_withOnlyUser_shouldReturnUserPermissions() {
    final User user = new User();
    final Permission permission = new Permission();
    final Resource resource = new Resource();
    final Application app = new Application();
    resource.setApplication(app);
    permission.setResource(resource);
    user.setPermissions(Set.of(permission));

    when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.of(user));

    final List<PermissionDTO> result =
        underTest.getPermissions(Optional.of(USER_ID), Optional.empty());

    assertEquals(1, result.size());
  }

  @Test
  void getPermissions_withOnlyApp_shouldReturnAppPermissions() {
    final Application app = new Application();
    app.setId(APP_ID);

    final Resource resource = new Resource();
    resource.setApplication(app);

    final Permission permission = new Permission();
    permission.setResource(resource);

    when(mockApplicationRepository.findById(APP_ID)).thenReturn(Optional.of(app));
    when(mockPermissionRepository.findAll()).thenReturn(List.of(permission));

    final List<PermissionDTO> result =
        underTest.getPermissions(Optional.empty(), Optional.of(APP_ID));

    assertEquals(1, result.size());
  }

  @Test
  void getPermissions_withNoUserOrApp_shouldReturnAllPermissions() {

    when(mockPermissionRepository.findAll())
        .thenReturn(
            List.of(
                buildPermission(PERMISSION_ID, APP_ID, RESOURCE_ID),
                buildPermission(2L, 202L, 301L)));

    final List<PermissionDTO> result = underTest.getPermissions(Optional.empty(), Optional.empty());

    assertEquals(2, result.size());
  }

  private Permission buildPermission(
      final Long permissionId, final Long appId, final Long resourceId) {
    final Permission permission = new Permission();
    permission.setId(permissionId);
    permission.setAction(new Action());
    permission.setResource(buildResource(appId, resourceId, Set.of(permission)));
    return permission;
  }

  private Resource buildResource(
      final Long appId, final Long resourceId, final Set<Permission> permission) {
    final Resource resource = new Resource();
    resource.setId(resourceId);
    resource.setPermissions(permission);
    final Application app = buildApplication(appId);
    resource.setApplication(app);
    return resource;
  }

  private Application buildApplication(final Long appId) {
    final Application application = new Application();
    application.setId(appId);
    return application;
  }
}
