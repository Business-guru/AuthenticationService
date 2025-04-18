package com.example.AuthenticationService.service;

import com.example.AuthenticationService.entity.User;
import com.example.AuthenticationService.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final  UserRepository userRepository;
    public void sendVerificationEmail(User user)
    {
        String verificationLink = "http://localhost:8083/api/v1/auth/verify?token=" + user.getToken();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmailId());
        message.setSubject("Verify Your Email");
        message.setText("Click the link to verify your email: " + verificationLink);
        mailSender.send(message);
    }
    public String verifyEmail(String token) {
        Optional<User> result = userRepository.findByToken(token);
        if (result.isPresent()) {
            User user = result.get();
            user.setVerified(true);
            userRepository.save(user);
            return "Email successfully verified";
        }
        return "Invalid verification link!";
    }
}
