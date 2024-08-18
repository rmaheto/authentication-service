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
}
