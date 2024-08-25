package com.codemaniac.authenticationservice.security;

import com.codemaniac.authenticationservice.exception.AuthenticationException;
import com.codemaniac.authenticationservice.model.User;
import com.codemaniac.authenticationservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {
  private static final org.slf4j.Logger Logger = LoggerFactory.getLogger("com.codemaniac.security");

    @Autowired
    private UserRepository userRepository;
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLogonId(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        if(!user.isEnabled()){
          Logger.warn("Authentication failed for user '{}': User is disabled",
              username);
          throw new AuthenticationException("User is disabled");
        }

        // Adding user-specific permissions
        Set<SimpleGrantedAuthority> authorities= new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));

        return new org.springframework.security.core.userdetails.User(user.getLogonId(), user.getPassword(), authorities);
    }
}
