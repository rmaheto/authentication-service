package com.codemaniac.authenticationservice.repository;

import com.codemaniac.authenticationservice.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<Application,Long> {
    Application findByName(String name);
    Application findByDomain(String domain); // Add method to find by domain
}
