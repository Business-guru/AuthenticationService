package com.example.AuthenticationService.controller;

import com.example.AuthenticationService.config.JwtService;
import com.example.AuthenticationService.dto.AuthenticationRequest;
import com.example.AuthenticationService.dto.AuthenticationResponse;
import com.example.AuthenticationService.dto.RegisterRequest;
import com.example.AuthenticationService.dto.UserDto;
import com.example.AuthenticationService.service.EmailService;
import com.example.AuthenticationService.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;
    private final EmailService emailService;
    private final JwtService jwtService;
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest)
    {
       return ResponseEntity.ok(userService.register(registerRequest));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request)
    {
        return ResponseEntity.ok(userService.authenticate(request));
    }

    @GetMapping("/verify")
    public  ResponseEntity<String> verifyAccount(@RequestParam("token") String token)
    {
       return ResponseEntity.ok(emailService.verifyEmail(token));
    }
    @GetMapping("/getUsername")
    public ResponseEntity<String> getUserId(@RequestHeader("Authorization") String token)
    {
        String parseToken= token.substring(7);
        return ResponseEntity.ok(jwtService.extractUserId(parseToken));
    }
}