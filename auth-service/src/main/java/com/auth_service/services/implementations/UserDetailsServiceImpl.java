package com.auth_service.services.implementations;

import com.auth_service.models.UserEntity;
import com.auth_service.repositories.UserRepository;
import com.auth_service.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws ResourceNotFoundException {
       Optional<UserEntity> userOp = userRepository.findByEmail(email);
        if (userOp.isEmpty()) {
            throw new ResourceNotFoundException("User not found with email: " + email);
        }
        UserEntity user = userOp.get();
        return User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRoles().toArray(new String[0]))
                .build();
    }
}