package com.auth_service.services.interfaces;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {   
    String generateToken(UserDetails user);
    boolean validateToken(String token, String username);
}