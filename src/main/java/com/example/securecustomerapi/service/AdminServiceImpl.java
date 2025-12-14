package com.example.securecustomerapi.service;

import com.example.securecustomerapi.dto.UpdateRoleDTO;
import com.example.securecustomerapi.dto.UserResponseDTO;
import com.example.securecustomerapi.entity.User;
import com.example.securecustomerapi.exception.ResourceNotFoundException;
import com.example.securecustomerapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public List<UserResponseDTO> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    @Override
    public UserResponseDTO updateUserRole(Long id, UpdateRoleDTO dto) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setRole(dto.getRole());
        User savedUser = userRepository.save(user);

        return convertToDTO(savedUser);
    }


    @Override
    public UserResponseDTO toggleUserStatus(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setIsActive(!user.getIsActive());
        User savedUser = userRepository.save(user);

        return convertToDTO(savedUser);
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
