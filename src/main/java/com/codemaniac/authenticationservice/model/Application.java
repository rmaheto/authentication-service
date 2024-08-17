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
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    private String name;
    private String domain;

    @OneToMany(mappedBy = "application", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Resource> resources = new HashSet<>();
    @Embedded
    private Audit audit;
}
