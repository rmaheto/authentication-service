//package com.codemaniac.authenticationservice;
//
//import com.codemaniac.authenticationservice.model.*;
//import com.codemaniac.authenticationservice.service.ApplicationService;
//import com.codemaniac.authenticationservice.service.PermissionService;
//import com.codemaniac.authenticationservice.service.ResourceService;
//import com.codemaniac.authenticationservice.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.HashSet;
//import java.util.Set;
//
//@Component
//@Transactional
//public class DataInitializer implements CommandLineRunner {
//
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private ApplicationService applicationService;
//
//    @Autowired
//    private PermissionService permissionService;
//    @Autowired
//    private ResourceService resourceService;
//
//    @Override
//    public void run(String... args) throws Exception {
//        // Create applications
//        Application app1 = applicationService.registerApplication("App1", "app1.com");
//        Application app2 = applicationService.registerApplication("App2", "app2.com");
//
//        // Create resources for applications
//        applicationService.addResourceToApplication(app1, "AuditReport");
//        applicationService.addResourceToApplication(app1, "UserManagement");
//        applicationService.addResourceToApplication(app2, "OrderManagement");
//
//        // Create actions for resources
//        Action readAction = new Action();
//        readAction.setRead(true);
//        readAction.setCreate(false);
//        readAction.setUpdate(false);
//        readAction.setDelete(false);
//
//        Action createAction = new Action();
//        createAction.setRead(false);
//        createAction.setCreate(true);
//        createAction.setUpdate(false);
//        createAction.setDelete(false);
//
//        Action updateAction = new Action();
//        updateAction.setRead(false);
//        updateAction.setCreate(false);
//        updateAction.setUpdate(true);
//        updateAction.setDelete(false);
//
//        Action deleteAction = new Action();
//        deleteAction.setRead(false);
//        deleteAction.setCreate(false);
//        deleteAction.setUpdate(false);
//        deleteAction.setDelete(true);
//
//        // Create permissions
//        Permission auditReportRead = new Permission();
//        auditReportRead.setResource(resourceService.findById(1L).get());
//        auditReportRead.setAction(readAction);
//        permissionService.addPermission(auditReportRead);
//
//        Permission auditReportCreate = new Permission();
//        auditReportCreate.setResource(resourceService.findById(1L).get());
//        auditReportCreate.setAction(createAction);
//        permissionService.addPermission(auditReportCreate);
//
//        Permission userManagementRead = new Permission();
//        userManagementRead.setResource(resourceService.findById(2L).get());
//        userManagementRead.setAction(readAction);
//        permissionService.addPermission(userManagementRead);
//
//        Permission orderManagementRead = new Permission();
//        orderManagementRead.setResource(resourceService.findById(3L).get());
//        orderManagementRead.setAction(readAction);
//        permissionService.addPermission(orderManagementRead);
//
//        // Create users
//        User adminUser = userService.registerUser("admin", "admin", Role.ADMIN);
//        User regularUser = userService.registerUser("user", "user", Role.USER);
//
//        // Assign permissions to users
//        Set<Permission> adminPermissions = new HashSet<>();
//        adminPermissions.add(auditReportRead);
//        adminPermissions.add(auditReportCreate);
//        adminPermissions.add(userManagementRead);
//        adminPermissions.add(orderManagementRead);
//
//        Set<Permission> userPermissions = new HashSet<>();
//        userPermissions.add(auditReportRead);
//
//        userService.assignPermissionsToUser(adminUser, adminPermissions);
//        userService.assignPermissionsToUser(regularUser, userPermissions);
//    }
//}
