package com.jtarcio.shrimpfarm.api.controller;

import com.jtarcio.shrimpfarm.application.dto.request.LoginRequest;
import com.jtarcio.shrimpfarm.application.dto.response.UsuarioResponse;
import com.jtarcio.shrimpfarm.application.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<UsuarioResponse> login(@Valid @RequestBody LoginRequest request) {
        UsuarioResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
