package com.example.online.authentication.authenticate.dto;

import com.example.online.document.dto.DocumentRequestDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class RegisterRequest {
    @NotNull(message = "First name cannot be empty")
    private String firstName;
    @NotNull(message = "Last name cannot be empty")
    private String lastName;
    @NotNull(message = "Email cannot be empty")
    private String email;
    @NotNull(message = "Password cannot be empty")
    private String password;

    private List<DocumentRequestDTO> docs;
}
