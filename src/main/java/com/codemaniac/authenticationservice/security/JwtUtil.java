package com.codemaniac.authenticationservice.security;

import com.codemaniac.authenticationservice.model.Action;
import com.codemaniac.authenticationservice.model.Permission;
import com.codemaniac.authenticationservice.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

  private static final String SECRET_KEY = "secret";

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().setSigningKey(Base64.getEncoder().encodeToString(SECRET_KEY.getBytes()))
        .parseClaimsJws(token).getBody();
  }

  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  public String generateToken(User user, String audience) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("iss", "com.ehi.auth");
    claims.put("sub", user.getLogonId());
    claims.put("com.ehi.pfl", "com.ehi.multi-audience-abac");
    claims.put("aud", Collections.singletonList(audience));

    Map<String, Boolean> permissions = extractPermissions(user, audience);

    claims.put("com.ehi.abac-perm." + audience, permissions);
    claims.put("com.ehi.abac-loc." + audience, new HashMap<>());

    return createToken(claims, user.getLogonId());
  }

  private Map<String, Boolean> extractPermissions(User user, String audience) {
    return user.getPermissions().stream()
        .filter(
            permission -> permission.getResource().getApplication().getDomain().equals(audience))
        .flatMap(permission -> {
          Map<String, Boolean> permissionMap = new HashMap<>();
          Action action = permission.getAction();
          String resourceName = permission.getResource().getName();

          if (Boolean.TRUE.equals(action.getRead())) {
            permissionMap.put(resourceName + ":READ", true);
          }
          if (Boolean.TRUE.equals(action.getCreate())) {
            permissionMap.put(resourceName + ":CREATE", true);
          }
          if (Boolean.TRUE.equals(action.getUpdate())) {
            permissionMap.put(resourceName + ":UPDATE", true);
          }
          if (Boolean.TRUE.equals(action.getDelete())) {
            permissionMap.put(resourceName + ":DELETE", true);
          }

          return permissionMap.entrySet().stream();
        })
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private String createToken(Map<String, Object> claims, String subject) {
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
        .signWith(SignatureAlgorithm.HS256,
            Base64.getEncoder().encodeToString(SECRET_KEY.getBytes()))
        .compact();
  }

  public Boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }
}
