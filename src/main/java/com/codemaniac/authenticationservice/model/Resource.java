package com.codemaniac.authenticationservice.model;


import com.codemaniac.authenticationservice.model.audit.Audit;
import com.codemaniac.authenticationservice.model.audit.AuditInterceptor;
import com.codemaniac.authenticationservice.model.audit.Auditable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditInterceptor.class)
public class Resource implements Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;
  private String name;

  @ManyToOne
  @JoinColumn(name = "application_id")
  private Application application;

  @OneToMany(mappedBy = "resource", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @ToString.Exclude
  private Set<Permission> permissions = new HashSet<>();

  @Embedded
  private Audit audit =new Audit();

}
