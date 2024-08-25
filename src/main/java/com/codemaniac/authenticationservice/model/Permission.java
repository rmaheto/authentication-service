package com.codemaniac.authenticationservice.model;

import com.codemaniac.authenticationservice.model.audit.Audit;
import com.codemaniac.authenticationservice.model.audit.AuditInterceptor;
import com.codemaniac.authenticationservice.model.audit.Auditable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditInterceptor.class)
public class Permission implements Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne
    @JoinColumn(name = "resource_id")
    private Resource resource;
    @Embedded
    private Action action;

    @ManyToMany(mappedBy = "permissions")
    private Set<User> users = new HashSet<>();

    @Embedded
    private Audit audit = new Audit();

}
