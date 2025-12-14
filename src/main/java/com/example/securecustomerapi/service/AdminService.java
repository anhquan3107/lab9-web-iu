package com.example.securecustomerapi.service;

import com.example.securecustomerapi.dto.UpdateRoleDTO;
import com.example.securecustomerapi.dto.UserResponseDTO;

import java.util.List;

public interface AdminService {

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO updateUserRole(Long id, UpdateRoleDTO dto);

    UserResponseDTO toggleUserStatus(Long id);
}
