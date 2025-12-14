package com.example.securecustomerapi.service;

import com.example.securecustomerapi.dto.LoginRequestDTO;
import com.example.securecustomerapi.dto.LoginResponseDTO;
import com.example.securecustomerapi.dto.RegisterRequestDTO;
import com.example.securecustomerapi.dto.UpdateProfileDTO;
import com.example.securecustomerapi.dto.UserResponseDTO;

public interface UserService {
    
    LoginResponseDTO login(LoginRequestDTO loginRequest);
    
    UserResponseDTO register(RegisterRequestDTO registerRequest);
    
    UserResponseDTO getCurrentUser(String username);

    UserResponseDTO updateProfile(String username, UpdateProfileDTO dto);

    void deleteAccount(String username, String password);
}
