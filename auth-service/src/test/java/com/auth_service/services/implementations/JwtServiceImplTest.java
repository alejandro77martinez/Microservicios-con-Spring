package com.auth_service.services.implementations;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceImplTest {

    @Test
    void validateTokenShouldReturnTrueForValidGeneratedToken() {
        JwtServiceImpl jwtService = new JwtServiceImpl();
        ReflectionTestUtils.setField(jwtService, "secret", "12345678901234567890123456789012");
        ReflectionTestUtils.setField(jwtService, "expiration", 60000L);

        UserDetails user = new User(
                "user@test.com",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String token = jwtService.generateToken(user);

        assertTrue(jwtService.validateToken(token, "user@test.com"));
    }

    @Test
    void validateTokenShouldReturnFalseForDifferentUsername() {
        JwtServiceImpl jwtService = new JwtServiceImpl();
        ReflectionTestUtils.setField(jwtService, "secret", "12345678901234567890123456789012");
        ReflectionTestUtils.setField(jwtService, "expiration", 60000L);

        UserDetails user = new User(
                "user@test.com",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String token = jwtService.generateToken(user);

        assertFalse(jwtService.validateToken(token, "other@test.com"));
    }

    @Test
    void validateTokenShouldReturnFalseForExpiredToken() {
        JwtServiceImpl jwtService = new JwtServiceImpl();
        ReflectionTestUtils.setField(jwtService, "secret", "12345678901234567890123456789012");
        ReflectionTestUtils.setField(jwtService, "expiration", -1000L);

        UserDetails user = new User(
                "user@test.com",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String token = jwtService.generateToken(user);

        assertFalse(jwtService.validateToken(token, "user@test.com"));
    }

    @Test
    void validateTokenShouldReturnFalseForMalformedToken() {
        JwtServiceImpl jwtService = new JwtServiceImpl();
        ReflectionTestUtils.setField(jwtService, "secret", "12345678901234567890123456789012");
        ReflectionTestUtils.setField(jwtService, "expiration", 60000L);

        assertFalse(jwtService.validateToken("not-a-jwt", "user@test.com"));
    }
}
