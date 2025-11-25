package com.example.online.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AuthenticationRequest {
    @NotNull(message = "Không được để trống Email")
    private String email;

    @NotNull(message = "Không được để trống mật khẩu")
    private String password;
}
