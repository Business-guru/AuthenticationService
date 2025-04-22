package com.example.AuthenticationService.service;

import com.example.AuthenticationService.dto.UserSyncDto;
import com.example.AuthenticationService.entity.User;
import com.example.AuthenticationService.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final  UserRepository userRepository;
    private final KafkaTemplate<String,Object> kafkaTemplate;
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
        Optional<User>  userdetails = userRepository.findByToken(token);
        if (userdetails.isPresent()) {
            User user = userdetails.get();
            user.setVerified(true);
            UserSyncDto userSyncDto=UserSyncDto.builder().email(user.getEmailId()).userId(user.getUserIdentity()).userType(user.getRole()).build();
            try {
                CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send("profile",userSyncDto);
                future.whenComplete((result, ex) -> {
                    if (ex == null) {
                        System.out.println("Sent message=[" + user.toString() +
                                "] with offset=[" + result.getRecordMetadata().offset() + "]");
                    } else {
                        System.out.println("Unable to send message=[" +
                                user.toString() + "] due to : " + ex.getMessage());
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            userRepository.save(user);
            return "Email successfully verified";
        }
        return "Invalid verification link!";
    }
}
