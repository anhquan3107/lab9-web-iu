package com.example.securecustomerapi.service;

import com.example.securecustomerapi.dto.*;
import com.example.securecustomerapi.entity.RefreshToken;
import com.example.securecustomerapi.entity.Role;
import com.example.securecustomerapi.entity.User;
import com.example.securecustomerapi.exception.DuplicateResourceException;
import com.example.securecustomerapi.exception.ResourceNotFoundException;
import com.example.securecustomerapi.repository.RefreshTokenRepository;
import com.example.securecustomerapi.repository.UserRepository;
import com.example.securecustomerapi.security.JwtTokenProvider;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
private RefreshTokenRepository refreshTokenRepository;
    
@Override
public LoginResponseDTO login(LoginRequestDTO loginRequest) {

    // 1. Authenticate
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequest.getUsername(),
            loginRequest.getPassword()
        )
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);

    // 2. Generate access token
    String accessToken = tokenProvider.generateToken(authentication);

    // 3. Get user
    User user = userRepository.findByUsername(loginRequest.getUsername())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    // 4. Remove old refresh tokens
    refreshTokenRepository.deleteByUserId(user.getId());

    // 5. Generate refresh token
    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setUser(user);
    refreshToken.setToken(UUID.randomUUID().toString());
    refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));

    refreshTokenRepository.save(refreshToken);

    // 6. Return both tokens
    return new LoginResponseDTO(
            accessToken,
            refreshToken.getToken(),
            user.getUsername(),
            user.getEmail(),
            user.getRole().name()
    );
}
    
    @Override
    public UserResponseDTO register(RegisterRequestDTO registerRequest) {
        // Check if username exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }
        
        // Check if email exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFullName());
        user.setRole(Role.USER);  // Default role
        user.setIsActive(true);
        
        User savedUser = userRepository.save(user);
        
        return convertToDTO(savedUser);
    }
    
    @Override
    public UserResponseDTO getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return convertToDTO(user);
    }

     @Override
    public UserResponseDTO updateProfile(String username, UpdateProfileDTO dto) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setFullName(dto.getFullName());

        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new DuplicateResourceException("Email already exists");
            }
            user.setEmail(dto.getEmail());
        }

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    @Override
    public void deleteAccount(String username, String password) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Password is incorrect");
        }

        user.setIsActive(false);
        userRepository.save(user);
    }
    
    private UserResponseDTO convertToDTO(User user) {
        return new UserResponseDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFullName(),
            user.getRole().name(),
            user.getIsActive(),
            user.getCreatedAt()
        );
    }
}
