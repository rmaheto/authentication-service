package com.codemaniac.authenticationservice.repository;

import com.codemaniac.authenticationservice.model.Application;
import com.codemaniac.authenticationservice.model.Resource;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

  List<Resource> findByApplication(Application application);
}
