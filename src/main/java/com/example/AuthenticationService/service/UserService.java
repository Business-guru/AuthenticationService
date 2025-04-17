package com.example.AuthenticationService.service;

import com.example.AuthenticationService.config.JwtService;
import com.example.AuthenticationService.dto.AuthenticationRequest;
import com.example.AuthenticationService.dto.AuthenticationResponse;
import com.example.AuthenticationService.dto.RegisterRequest;
import com.example.AuthenticationService.entity.User;
import com.example.AuthenticationService.enums.Role;
import com.example.AuthenticationService.exceptionHandler.EmailNotVerifiedException;
import com.example.AuthenticationService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    public String register(RegisterRequest registerRequest) {
        try {
            String token= UUID.randomUUID().toString();
            var user= User.builder()
                    .userName(registerRequest.getUserName())
                    .emailId(registerRequest.getEmailId())
                    .role(registerRequest.getRole())
                    .isVerified(false)
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .role(registerRequest.getRole())
                    .token(token)
                    .build();
            emailService.sendVerificationEmail(user);
            userRepository.save(user);

            return "User successfully created";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmailId(),
                        request.getPassword()
                )
        );

        var user=userRepository.findByEmailId(request.getEmailId()).orElseThrow(()->new NullPointerException());
         if(!userRepository.findIsVerifiedByEmail(request.getEmailId()))
         {
             throw new EmailNotVerifiedException("Email is not verified");
         }
        var jwtToken= jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }
}
