package com.codemaniac.authenticationservice.model;

import com.codemaniac.authenticationservice.model.audit.Audit;
import com.codemaniac.authenticationservice.model.audit.AuditInterceptor;

import com.codemaniac.authenticationservice.model.audit.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "users")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditInterceptor.class)
public class User implements Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;
  @Column(nullable = false, unique = true)
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
  private Set<Application> applications = new HashSet<>();

  @Embedded
  private Audit audit = new Audit();
}
