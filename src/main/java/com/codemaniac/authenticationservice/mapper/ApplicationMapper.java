package com.codemaniac.authenticationservice.mapper;

import com.codemaniac.authenticationservice.dto.ApplicationDTO;
import com.codemaniac.authenticationservice.model.Application;
import jakarta.annotation.Nonnull;

public class ApplicationMapper {

  private ApplicationMapper(){}
  public static ApplicationDTO toDTO(@Nonnull final Application application) {
    final ApplicationDTO applicationDTO = new ApplicationDTO();
    applicationDTO.setId(application.getId());
    applicationDTO.setName(application.getName());
    applicationDTO.setDomain(application.getDomain());
    return applicationDTO;
  }

  public static Application toEntity(@Nonnull final ApplicationDTO applicationDTO) {
    final Application application = new Application();
    application.setId(applicationDTO.getId());
    application.setName(applicationDTO.getName());
    application.setDomain(applicationDTO.getDomain());
    return application;
  }
}
