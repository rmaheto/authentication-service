package com.codemaniac.authenticationservice.mapper;

import com.codemaniac.authenticationservice.dto.ApplicationDTO;
import com.codemaniac.authenticationservice.model.Application;

public class ApplicationMapper {

  private ApplicationMapper(){}
  public static ApplicationDTO toDTO(Application application) {
    ApplicationDTO applicationDTO = new ApplicationDTO();
    applicationDTO.setId(application.getId());
    applicationDTO.setName(application.getName());
    applicationDTO.setDomain(application.getDomain());
    return applicationDTO;
  }

  public static Application toEntity(ApplicationDTO applicationDTO) {
    Application application = new Application();
    application.setId(applicationDTO.getId());
    application.setName(applicationDTO.getName());
    application.setDomain(applicationDTO.getDomain());
    return application;
  }
}
