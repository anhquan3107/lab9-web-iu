package com.example.securecustomerapi.controller;

import com.example.securecustomerapi.dto.UpdateProfileDTO;
import com.example.securecustomerapi.dto.UserResponseDTO;
import com.example.securecustomerapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    // ===============================
    // TASK 7.1 – VIEW PROFILE
    // ===============================
    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getProfile() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        UserResponseDTO user = userService.getCurrentUser(username);
        return ResponseEntity.ok(user);
    }

    // ===============================
    // TASK 7.2 – UPDATE PROFILE
    // ===============================
    @PutMapping("/profile")
    public ResponseEntity<UserResponseDTO> updateProfile(
            @Valid @RequestBody UpdateProfileDTO dto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        UserResponseDTO updatedUser = userService.updateProfile(username, dto);
        return ResponseEntity.ok(updatedUser);
    }

    // ===============================
    // TASK 7.3 – DELETE ACCOUNT
    // ===============================
    @DeleteMapping("/account")
    public ResponseEntity<?> deleteAccount(@RequestParam String password) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        userService.deleteAccount(username, password);
        return ResponseEntity.ok("Account deleted successfully");
    }
}
