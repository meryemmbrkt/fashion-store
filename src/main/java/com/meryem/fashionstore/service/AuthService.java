package com.meryem.fashionstore.service;

import com.meryem.fashionstore.dto.LoginRequest;
import com.meryem.fashionstore.dto.LoginResponse;
import com.meryem.fashionstore.dto.RefreshRequest;
import com.meryem.fashionstore.entity.User;
import com.meryem.fashionstore.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(), request.password())
        );

        User user = userService.getUserByEmail(request.email());

        String token = jwtService.generateToken(
                user.getEmail(), user.getId(), user.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return new LoginResponse(
                token, refreshToken, user.getId(), user.getEmail(), user.getRole());
    }

    public LoginResponse refresh(RefreshRequest request) {
        String refreshToken = request.refreshToken();

        if (!jwtService.isTokenValid(refreshToken)
                || !jwtService.isRefreshToken(refreshToken)) {
            throw new AccessDeniedException(
                    "Geçersiz veya süresi dolmuş refresh token!");
        }

        String email = jwtService.extractEmail(refreshToken);
        User user = userService.getUserByEmail(email);

        String newToken = jwtService.generateToken(
                user.getEmail(), user.getId(), user.getRole().name());
        String newRefreshToken = jwtService.generateRefreshToken(user.getEmail());

        return new LoginResponse(
                newToken, newRefreshToken, user.getId(), user.getEmail(), user.getRole());
    }
}