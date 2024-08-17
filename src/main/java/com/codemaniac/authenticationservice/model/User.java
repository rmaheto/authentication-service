package com.codemaniac.authenticationservice.model;

import com.codemaniac.authenticationservice.model.audit.Audit;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "users")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;
  private String logonId;
  private String password;
  private boolean enabled;

  private Role role;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "users_permissions",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "permission_id")
  )
  @ToString.Exclude
  private Set<Permission> permissions = new HashSet<>();

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "users_applications",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "application_id")
  )
  @ToString.Exclude
  private Set<Application> applications = new HashSet<>();
  @Embedded
  private Audit audit;
}
