package com.codemaniac.authenticationservice.repository;


import com.codemaniac.authenticationservice.model.Permission;
import com.codemaniac.authenticationservice.model.Resource;
import com.codemaniac.authenticationservice.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission,Long> {

  @Query("SELECT p FROM Permission p JOIN p.users u WHERE u = :user AND p.resource = :resource")
  Optional<Permission> findByUserAndResource(@Param("user") User user, @Param("resource") Resource resource);
}
