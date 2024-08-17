package com.codemaniac.authenticationservice.repository;

import com.codemaniac.authenticationservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByLogonId(String logonId);
}
