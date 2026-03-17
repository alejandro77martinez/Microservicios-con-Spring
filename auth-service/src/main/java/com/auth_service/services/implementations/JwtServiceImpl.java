package com.auth_service.services.implementations;

import com.auth_service.services.interfaces.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration}")
  private long expiration;

  @Override
  public String generateToken(UserDetails user) {
    return Jwts.builder()
      .setSubject(user.getUsername())
      .claim("roles", user.getAuthorities())
      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis() + expiration))
      .signWith(getKey(), SignatureAlgorithm.HS256)
      .compact();
  }

  @Override
  public boolean validateToken(String token) {
    return (!isTokenExpired(token));
  }

  @Override
  public String getUserFromToken(String token) {
    return extractUsername(token);
  }

  private String extractUsername(String token) {
    try {
      return Jwts.parserBuilder()
        .setSigningKey(getKey())
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
    } catch (Exception e) {
      return ""; // Invalid token
    }
  }

  private boolean isTokenExpired(String token) {
    try {
      final Date expiration = Jwts.parserBuilder()
        .setSigningKey(getKey())
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getExpiration();
      return expiration.before(new Date());
    } catch (Exception e) {
      return true; // Treat invalid token as expired
    }
  }

  private Key getKey() {
    return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  @Override
  public String refreshToken(String token) {
    try {
      final Date now = new Date();
      final var claims = Jwts.parserBuilder()
        .setSigningKey(getKey())
        .build()
        .parseClaimsJws(token)
        .getBody();

      final Date tokenExpiration = claims.getExpiration();
      if (tokenExpiration == null || tokenExpiration.before(now)) {
        return "";
      }

      return Jwts.builder()
        .setSubject(claims.getSubject())
        .claim("roles", claims.get("roles"))
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + expiration))
        .signWith(getKey(), SignatureAlgorithm.HS256)
        .compact();
    } catch (Exception e) {
      return ""; // Invalid token
    }
  }
}
