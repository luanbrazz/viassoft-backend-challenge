package com.viassoft.emailservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viassoft.emailservice.dto.EmailAwsDTO;
import com.viassoft.emailservice.dto.EmailRequestDTO;
import com.viassoft.emailservice.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${mail.integracao}")
    private String mailIntegration;

    private final ObjectMapper objectMapper;

    @Override
    public void processEmail(EmailRequestDTO request) {
        try {
            if ("AWS".equalsIgnoreCase(mailIntegration)) {
                EmailAwsDTO awsDTO = adaptToAws(request);
                logToConsole(awsDTO);
            } else {
                throw new IllegalArgumentException("Invalid mail integration configuration");
            }
        } catch (Exception ex) {
            log.error("Error processing email: {}", ex.getMessage());
            throw new RuntimeException("Error processing email: " + ex.getMessage());
        }
    }

    private EmailAwsDTO adaptToAws(EmailRequestDTO request) {
        return EmailAwsDTO.builder()
                .recipient(request.getRecipient())
                .recipientName(request.getRecipientName())
                .sender(request.getSender())
                .subject(request.getSubject())
                .content(request.getContent())
                .build();
    }

    private void logToConsole(Object dto) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(dto);
        log.info("Serialized email DTO: {}", json);
    }
}
