package com.viassoft.emailservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viassoft.emailservice.dto.EmailAwsDTO;
import com.viassoft.emailservice.dto.EmailOciDTO;
import com.viassoft.emailservice.dto.EmailRequestDTO;
import com.viassoft.emailservice.entity.EmailAudit;
import com.viassoft.emailservice.entity.enums.EmailStatus;
import com.viassoft.emailservice.repository.EmailAuditRepository;
import com.viassoft.emailservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${mail.integracao}")
    private String mailIntegration;

    private final ObjectMapper objectMapper;
    private final EmailAuditRepository emailAuditRepository;

    @Override
    public void processEmail(EmailRequestDTO request) {
        try {
            if ("AWS".equalsIgnoreCase(mailIntegration)) {
                EmailAwsDTO awsDTO = adaptToAws(request);
                logToConsole(awsDTO);
                saveAudit(request, EmailStatus.SUCCESS, null);
            } else if ("OCI".equalsIgnoreCase(mailIntegration)) {
                EmailOciDTO ociDTO = adaptToOci(request);
                logToConsole(ociDTO);
                saveAudit(request, EmailStatus.SUCCESS, null);
            } else {
                throw new IllegalArgumentException("Invalid mail integration configuration");
            }
        } catch (Exception ex) {
            log.error("Error processing email: {}", ex.getMessage());
            saveAudit(request, EmailStatus.FAILURE, ex.getMessage());
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

    private EmailOciDTO adaptToOci(EmailRequestDTO request) {
        return EmailOciDTO.builder()
                .recipientEmail(request.getRecipient())
                .recipientName(request.getRecipientName())
                .senderEmail(request.getSender())
                .subject(request.getSubject())
                .body(request.getContent())
                .build();
    }

    private void logToConsole(Object dto) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(dto);
        log.info("Serialized email DTO: {}", json);
    }

    private void saveAudit(EmailRequestDTO request, EmailStatus status, String errorMessage) {
        EmailAudit emailAudit = EmailAudit.builder()
                .recipient(request.getRecipient())
                .recipientName(request.getRecipientName())
                .sender(request.getSender())
                .subject(request.getSubject())
                .content(request.getContent())
                .status(status)
                .errorMessage(errorMessage)
                .timestamp(LocalDateTime.now())
                .integrationType(mailIntegration)
                .build();

        emailAuditRepository.save(emailAudit);
    }
}
