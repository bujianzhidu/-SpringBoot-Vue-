package com.example.tourism.controller;

import com.example.tourism.entity.User;
import com.example.tourism.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "User related operations")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<Void> register(@RequestBody User user) {
        userService.register(user);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update user profile")
    public ResponseEntity<Void> updateProfile(@RequestBody User user) {
        userService.updateProfile(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get user profile")
    public ResponseEntity<User> getProfile(@RequestParam String username) {
        return ResponseEntity.ok(userService.findByUsername(username));
    }
} 