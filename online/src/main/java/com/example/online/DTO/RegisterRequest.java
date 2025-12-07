package com.example.online.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

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
}
