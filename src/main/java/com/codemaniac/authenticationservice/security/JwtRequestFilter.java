package com.codemaniac.authenticationservice.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

  private final UserDetailsService userDetailsService;

  private final JwtUtil jwtUtil;

  public static final String AUTHORIZATION = "Authorization";
  public static final String BEARER = "Bearer ";

  @Override
  protected void doFilterInternal(@Nonnull final HttpServletRequest request, @Nonnull final HttpServletResponse response,
      @Nonnull final FilterChain chain) throws ServletException, IOException {
    final String authorizationHeader = request.getHeader(AUTHORIZATION);

    String username = null;
    String jwt = null;

    if (authorizationHeader != null && authorizationHeader.startsWith(BEARER)) {
      jwt = authorizationHeader.substring(7);
      try {
        username = jwtUtil.extractUsername(jwt);
      } catch (final ExpiredJwtException e) {
        log.warn("JWT expired");
      }
    }

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

      final UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

      if (Boolean.TRUE.equals(jwtUtil.validateToken(jwt, userDetails))) {

        final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
        usernamePasswordAuthenticationToken
            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
      }
    }
    chain.doFilter(request, response);
  }
}
