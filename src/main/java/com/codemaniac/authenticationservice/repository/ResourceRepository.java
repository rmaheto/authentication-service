package com.codemaniac.authenticationservice.repository;

import com.codemaniac.authenticationservice.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends JpaRepository<Resource,Long> {

}
