package com.codemaniac.authenticationservice.dto;

import lombok.Data;
@Data
public class ResourceDTO {

  private Long id;
  private String name;
  private ApplicationDTO application;

  // Default constructor
  public ResourceDTO() {
  }

  // Parameterized constructor
  public ResourceDTO(Long id, String name, ApplicationDTO application) {
    this.id = id;
    this.name = name;
    this.application = application;
  }

}
