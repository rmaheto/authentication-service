package com.codemaniac.authenticationservice.security;

import com.codemaniac.authenticationservice.model.audit.Audit;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtils {

  private SecurityUtils(){}
  public static  String getCurrentUsername() {
    final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof final UserDetails userDetails) {
      return userDetails.getUsername();
    }
    return Audit.SYSTEM; // Default to SYSTEM if no user is authenticated
  }
}
