package com.codemaniac.authenticationservice.model;


import com.codemaniac.authenticationservice.model.audit.Audit;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    private String name;

    @ManyToOne
    @JoinColumn(name = "application_id")
    private Application application;

    @OneToMany(mappedBy = "resource", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Permission> permissions = new HashSet<>();
    @Embedded
    private Audit audit;
}
