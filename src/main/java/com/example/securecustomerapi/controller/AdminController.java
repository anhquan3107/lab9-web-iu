package com.example.securecustomerapi.controller;

import com.example.securecustomerapi.dto.UpdateRoleDTO;
import com.example.securecustomerapi.dto.UserResponseDTO;
import com.example.securecustomerapi.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {

        List<UserResponseDTO> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @PutMapping("/users/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoleDTO dto) {

        UserResponseDTO updatedUser = adminService.updateUserRole(id, dto);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/users/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> toggleUserStatus(
            @PathVariable Long id) {

        UserResponseDTO updatedUser = adminService.toggleUserStatus(id);
        return ResponseEntity.ok(updatedUser);
    }
}
