package com.meryem.fashionstore.controller;
import com.meryem.fashionstore.dto.PagedUserResponse;
import com.meryem.fashionstore.dto.UserFilterRequest;
import com.meryem.fashionstore.dto.UserResponse;
import com.meryem.fashionstore.dto.UserUpdateDto;
import com.meryem.fashionstore.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<PagedUserResponse> getAllUsers(
            @RequestBody(required = false) UserFilterRequest filter) {
        return ResponseEntity.ok(userService.getAllUsers(filter));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDto userDto) {
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Kullanıcı başarıyla silindi.");
    }
}

