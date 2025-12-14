package com.example.securecustomerapi.service;

import com.example.securecustomerapi.dto.ChangePasswordDTO;
import com.example.securecustomerapi.dto.LoginResponseDTO;
import com.example.securecustomerapi.dto.RefreshTokenDTO;
import com.example.securecustomerapi.dto.ResetPasswordDTO;

public interface AuthService {

    void changePassword(ChangePasswordDTO dto);

    String forgotPassword(String email);

    void resetPassword(ResetPasswordDTO dto);

    LoginResponseDTO refreshToken(RefreshTokenDTO dto);

}