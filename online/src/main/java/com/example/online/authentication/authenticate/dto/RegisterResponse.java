package com.example.online.authentication.authenticate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RegisterResponse {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
