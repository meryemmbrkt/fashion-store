package com.meryem.fashionstore.controller;

import com.meryem.fashionstore.dto.LoginRequest;
import com.meryem.fashionstore.dto.LoginResponse;
import com.meryem.fashionstore.dto.RefreshRequest;
import com.meryem.fashionstore.dto.UserRequest;
import com.meryem.fashionstore.dto.UserResponse;
import com.meryem.fashionstore.entity.User;
import com.meryem.fashionstore.security.JwtService;
import com.meryem.fashionstore.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.saveUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userService.getUserByEmail(request.email());
        String token = jwtService.generateToken(
                user.getEmail(), user.getId(), user.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return ResponseEntity.ok(new LoginResponse(
                token, refreshToken, user.getId(), user.getEmail(), user.getRole()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        String refreshToken = request.refreshToken();

        if (!jwtService.isTokenValid(refreshToken) || !jwtService.isRefreshToken(refreshToken)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Geçersiz veya süresi dolmuş refresh token!"
            );
        }

        String email = jwtService.extractEmail(refreshToken);
        User user = userService.getUserByEmail(email);

        String newToken = jwtService.generateToken(
                user.getEmail(), user.getId(), user.getRole().name());
        String newRefreshToken = jwtService.generateRefreshToken(user.getEmail());

        return ResponseEntity.ok(new LoginResponse(
                newToken, newRefreshToken, user.getId(), user.getEmail(), user.getRole()));
    }
}