package com.codemaniac.authenticationservice.dto;

import lombok.Data;

@Data
public class ApplicationDTO {

  private Long id;
  private String name;
  private String domain;

  // Default constructor
  public ApplicationDTO() {
  }
  public ApplicationDTO(final Long id, final String name, final String domain) {
    this.id = id;
    this.name = name;
    this.domain = domain;
  }

  @Override
  public String toString() {
    return "ApplicationDTO{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", domain='" + domain + '\'' +
            '}';
  }
}
