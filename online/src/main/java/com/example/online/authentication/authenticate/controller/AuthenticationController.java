package com.example.online.authentication.authenticate.controller;

import com.example.online.authentication.authenticate.dto.AuthenticationRequest;
import com.example.online.authentication.authenticate.dto.AuthenticationResponse;
import com.example.online.authentication.authenticate.dto.RegisterRequest;
import com.example.online.authentication.authenticate.dto.RegisterResponse;
import com.example.online.authentication.authenticate.service.AuthenticationService;
import com.example.online.elasticsearch.service.IndexService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationController.class);

    // Role: Admin, User Use-case Role: None
    @Operation(summary = "Đăng nhập người dùng",
            description = "Đăng nhập người dùng và trả về JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Đăng nhập thành công",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class))),
    })
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody AuthenticationRequest request){
        LOG.info("POST /api/v1/auth/login - Body: AuthenticationRequest");
        return ResponseEntity.ok(authenticationService.authentication(request));
    }

    // Role: Admin, User Use-case Role: None
    @Operation(summary = "Đăng kí người dùng",
            description = "Đăng kí người dùng và trả về thông tin user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Đăng kí thành công",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegisterResponse.class))),
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerController(@Valid @RequestBody RegisterRequest registerRequest){
        LOG.info("POST /api/v1/auth/register - Body: RegisterRequest");
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.register(registerRequest));
    }
}
