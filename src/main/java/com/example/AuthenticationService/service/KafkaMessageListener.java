package com.example.AuthenticationService.service;

import com.example.AuthenticationService.dto.UserSyncDto;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

//@Service
//public class KafkaMessageListener {
//
//
//    @KafkaListener(topics = "profile", groupId = "jt-group")
//    public void consumeEvents(UserSyncDto userSyncDto) {
//        System.out.println(userSyncDto.toString());
//    }
//}