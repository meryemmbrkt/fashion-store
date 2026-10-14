package com.meryem.fashionstore.controller;

import com.meryem.fashionstore.dto.LoginRequest;
import com.meryem.fashionstore.dto.LoginResponse;
import com.meryem.fashionstore.dto.RefreshRequest;
import com.meryem.fashionstore.dto.UserRequest;
import com.meryem.fashionstore.dto.UserResponse;
import com.meryem.fashionstore.service.AuthService;
import com.meryem.fashionstore.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
//@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.saveUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestParam String username,@RequestParam String password) {
        LoginRequest request = new LoginRequest(username,password);
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }
}
