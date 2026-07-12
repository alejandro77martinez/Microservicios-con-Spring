package com.auth_service.services.implementations;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        assertTrue(jwtService.validateToken(token));
    }

    @Test
    void validateTokenShouldReturnFalseForExpiredToken() {
        JwtServiceImpl jwtService = new JwtServiceImpl();
        ReflectionTestUtils.setField(jwtService, "secret", "12345678901234567890123456789012");
        ReflectionTestUtils.setField(jwtService, "expiration", -1000L);
        UserDetails user = new User(
                "suza.ortiz@gmail.com",
                "holamundo",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        String token = jwtService.generateToken(user);
        assertFalse(jwtService.validateToken(token));
    }

    @Test
    void validateTokenShouldReturnFalseForMalformedToken() {
        JwtServiceImpl jwtService = new JwtServiceImpl();
        assertFalse(jwtService.validateToken("not-a-jwt"));
    }

    @Test
    void refreshTokenShouldReturnEmptyForExpiredToken() {
        JwtServiceImpl jwtService = new JwtServiceImpl();
        ReflectionTestUtils.setField(jwtService, "secret", "12345678901234567890123456789012");
        ReflectionTestUtils.setField(jwtService, "expiration", -1000L);
        UserDetails user = new User(
                "user@test.com",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String token = jwtService.generateToken(user);

        assertEquals("", jwtService.refreshToken(token));
    }

    @Test
    void getUserFromTokenShouldReturnEmptyForMalformedToken() {
        JwtServiceImpl jwtService = new JwtServiceImpl();
        ReflectionTestUtils.setField(jwtService, "secret", "12345678901234567890123456789012");

        assertEquals("", jwtService.getUserFromToken("not-a-jwt"));
    }
}
