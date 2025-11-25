package com.example.online.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RegisterRequest {
    @NotNull(message = "Không được để trống")
    private String firstName;
    @NotNull(message = "Không được để trống")
    private String lastName;
    @NotNull(message = "Không được để trống")
    private String email;
    @NotNull(message = "Không được để trống")
    private String password;
}
