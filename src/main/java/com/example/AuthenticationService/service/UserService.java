package com.example.AuthenticationService.service;

import com.example.AuthenticationService.config.JwtService;
import com.example.AuthenticationService.dto.AuthenticationRequest;
import com.example.AuthenticationService.dto.AuthenticationResponse;
import com.example.AuthenticationService.dto.RegisterRequest;
import com.example.AuthenticationService.entity.User;
import com.example.AuthenticationService.enums.Role;
import com.example.AuthenticationService.exceptionHandler.EmailNotVerifiedException;
import com.example.AuthenticationService.repository.UserRepository;
import kafka.Kafka;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final KafkaTemplate<String,Object> template;
    public String register(RegisterRequest registerRequest) {
        try {
            String email=registerRequest.getEmailId();
            if(!userRepository.existsByEmail(email)) {
                String token = UUID.randomUUID().toString();
                var user = User.builder()
                        .userIdentity(registerRequest.getUserIdentity())
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
            }
            else throw new RuntimeException("Email already exist");
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

            if (userRepository.findIsVerifiedByEmail(request.getEmailId())) {
                User user = userRepository.findByEmailId(request.getEmailId()).orElseThrow(() -> new NullPointerException());
                var jwtToken = jwtService.generateToken(user);
                return AuthenticationResponse.builder().token(jwtToken).build();
            } else {
                throw new EmailNotVerifiedException("Email is not verified");
            }
    }

}
