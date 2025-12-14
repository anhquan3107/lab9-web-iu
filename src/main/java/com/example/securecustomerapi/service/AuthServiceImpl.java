package com.example.securecustomerapi.service;

import com.example.securecustomerapi.dto.ChangePasswordDTO;
import com.example.securecustomerapi.dto.LoginResponseDTO;
import com.example.securecustomerapi.dto.RefreshTokenDTO;
import com.example.securecustomerapi.dto.ResetPasswordDTO;
import com.example.securecustomerapi.entity.RefreshToken;
import com.example.securecustomerapi.entity.User;
import com.example.securecustomerapi.exception.ResourceNotFoundException;
import com.example.securecustomerapi.repository.RefreshTokenRepository;
import com.example.securecustomerapi.repository.UserRepository;
import com.example.securecustomerapi.security.JwtTokenProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    // ===============================
    // TASK 6.1 – CHANGE PASSWORD
    // ===============================
    @Override
    public void changePassword(ChangePasswordDTO dto) {

        // 1. Get current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 2. Verify current password
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // 3. Confirm new password
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        // 4. Hash & update password
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }
        // ===============================
    // TASK 6.2 – FORGOT PASSWORD
    // ===============================
    @Override
    public String forgotPassword(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email not found"));

        String token = UUID.randomUUID().toString();

        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));

        userRepository.save(user);
        return token;
    }

    @Override
    public void resetPassword(ResetPasswordDTO dto) {

        User user = userRepository.findByResetToken(dto.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token expired");
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);

        userRepository.save(user);
    }

    @Override
    public LoginResponseDTO refreshToken(RefreshTokenDTO dto) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(dto.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        User user = refreshToken.getUser();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        null,
                        SecurityContextHolder.getContext()
                                .getAuthentication()
                                .getAuthorities()
                );

        String newAccessToken = tokenProvider.generateToken(authentication);

        return new LoginResponseDTO(
                newAccessToken,                 
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                refreshToken.getToken()         
        );
    }


}


