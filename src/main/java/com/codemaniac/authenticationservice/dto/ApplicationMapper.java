package com.codemaniac.authenticationservice.dto;

import com.codemaniac.authenticationservice.model.Application;

public class ApplicationMapper {

  public static ApplicationDTO convertToDTO(Application application){
   return new ApplicationDTO(application.getId(), application.getName(), application.getDomain());
  }
}
