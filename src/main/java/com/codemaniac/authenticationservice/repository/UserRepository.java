package com.codemaniac.authenticationservice.repository;

import com.codemaniac.authenticationservice.model.Application;
import com.codemaniac.authenticationservice.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByLogonId(String logonId);
  List<User> findByApplicationsContaining(Application application);
}
