package com.codemaniac.authenticationservice.security;

import com.codemaniac.authenticationservice.model.Action;
import com.codemaniac.authenticationservice.model.Permission;
import com.codemaniac.authenticationservice.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.Nonnull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

  private static final String SECRET_KEY = "secret";

  public String extractUsername(@Nonnull final String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public Date extractExpiration(@Nonnull final String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(@Nonnull final String token, final Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(@Nonnull final String token) {
    return Jwts.parser().setSigningKey(Base64.getEncoder().encodeToString(SECRET_KEY.getBytes()))
        .parseClaimsJws(token).getBody();
  }

  private Boolean isTokenExpired(@Nonnull final String token) {
    return extractExpiration(token).before(new Date());
  }

  public String generateToken(final User user, final String audience) {
    final Map<String, Object> claims = new HashMap<>();
    claims.put("iss", "com.ehi.auth");
    claims.put("sub", user.getLogonId());
    claims.put("com.ehi.pfl", "com.ehi.multi-audience-abac");
    claims.put("aud", Collections.singletonList(audience));

    final Map<String, Boolean> permissions = extractPermissions(user, audience);

    claims.put("com.ehi.abac-perm." + audience, permissions);
    claims.put("com.ehi.abac-loc." + audience, new HashMap<>());

    return createToken(claims, user.getLogonId());
  }

  private Map<String, Boolean> extractPermissions(final User user, final String audience) {
    return user.getPermissions().stream()
        .filter(
            permission -> permission.getResource().getApplication().getDomain().equals(audience))
        .flatMap(permission -> {
          final Map<String, Boolean> permissionMap = new HashMap<>();
          final Action action = permission.getAction();
          final String resourceName = permission.getResource().getName();

          if (action.isRead()) {
            permissionMap.put(resourceName + ":READ", true);
          }
          if (action.isCreate()) {
            permissionMap.put(resourceName + ":CREATE", true);
          }
          if (action.isUpdate()) {
            permissionMap.put(resourceName + ":UPDATE", true);
          }
          if (action.isDelete()) {
            permissionMap.put(resourceName + ":DELETE", true);
          }

          return permissionMap.entrySet().stream();
        })
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private String createToken(final Map<String, Object> claims, final String subject) {
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
        .signWith(SignatureAlgorithm.HS256,
            Base64.getEncoder().encodeToString(SECRET_KEY.getBytes()))
        .compact();
  }

  public Boolean validateToken(final String token, final UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }
}
