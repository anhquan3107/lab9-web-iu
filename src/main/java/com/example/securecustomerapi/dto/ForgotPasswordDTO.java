package com.example.securecustomerapi.dto;

import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordDTO {

    @NotBlank
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // getters & setters
}
