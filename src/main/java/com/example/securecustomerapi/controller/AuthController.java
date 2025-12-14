package com.example.securecustomerapi.controller;

import com.example.securecustomerapi.dto.*;
import com.example.securecustomerapi.service.AuthService;
import com.example.securecustomerapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO response = userService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        UserResponseDTO response = userService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        UserResponseDTO user = userService.getCurrentUser(username);
        return ResponseEntity.ok(user);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        // In JWT, logout is handled client-side by removing token
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully. Please remove token from client.");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDTO dto) {
        authService.changePassword(dto);
        return ResponseEntity.ok("Password changed successfully");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @Valid @RequestBody ForgotPasswordDTO dto) {

        String token = authService.forgotPassword(dto.getEmail());
        return ResponseEntity.ok(token); 
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @Valid @RequestBody ResetPasswordDTO dto) {

        authService.resetPassword(dto);
        return ResponseEntity.ok("Password reset successfully");
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refreshToken(
            @RequestBody RefreshTokenDTO dto) {

        LoginResponseDTO response = authService.refreshToken(dto);
        return ResponseEntity.ok(response);
    }

}
