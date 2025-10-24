package com.example.job_portal_spring_security.auth.AuthController;

import com.example.job_portal_spring_security.auth.AuthService.AuthService;
import com.example.job_portal_spring_security.auth.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest r) {
        return ResponseEntity.status(201).body(authService.register(r));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        // 200 OK for login
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(@RequestBody(required = false) LogoutRequest body,
                                                  @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = (body != null) ? body.getToken() : null;
        if ((token == null || token.isBlank()) && authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Token is required"));
        }
        return ResponseEntity.ok(authService.logout(token));
    }


}
