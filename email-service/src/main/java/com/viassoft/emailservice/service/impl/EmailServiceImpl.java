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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
                this.validateAwsRequest(request);
                EmailAwsDTO awsDTO = adaptToAws(request);
                this.logToConsole(awsDTO);
                this.saveAudit(request, EmailStatus.SUCCESS, null);
            } else if ("OCI".equalsIgnoreCase(mailIntegration)) {
                this.validateOciRequest(request);
                EmailOciDTO ociDTO = adaptToOci(request);
                logToConsole(ociDTO);
                this.saveAudit(request, EmailStatus.SUCCESS, null);
            } else {
                throw new IllegalArgumentException("Invalid mail integration configuration");
            }
        } catch (Exception ex) {
            log.error("Error processing email: {}", ex.getMessage());
            this.saveAudit(request, EmailStatus.FAILURE, ex.getMessage());
            throw new RuntimeException("Error processing email: " + ex.getMessage());
        }
    }

    @Override
    public Page<EmailAudit> findEmailsByStatus(EmailStatus status, Pageable pageable) {
        return emailAuditRepository.findByStatus(status, pageable);
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

    private void validateAwsRequest(EmailRequestDTO request) {
        if (request.getRecipient().length() > 45) {
            throw new IllegalArgumentException("AWS: Recipient email must not exceed 45 characters");
        }
        if (request.getRecipientName().length() > 60) {
            throw new IllegalArgumentException("AWS: Recipient name must not exceed 60 characters");
        }
        if (request.getSender().length() > 45) {
            throw new IllegalArgumentException("AWS: Sender email must not exceed 45 characters");
        }
        if (request.getSubject().length() > 120) {
            throw new IllegalArgumentException("AWS: Subject must not exceed 120 characters");
        }
        if (request.getContent().length() > 256) {
            throw new IllegalArgumentException("AWS: Content must not exceed 256 characters");
        }
    }

    private void validateOciRequest(EmailRequestDTO request) {
        if (request.getRecipient().length() > 40) {
            throw new IllegalArgumentException("OCI: Recipient email must not exceed 40 characters");
        }
        if (request.getRecipientName().length() > 50) {
            throw new IllegalArgumentException("OCI: Recipient name must not exceed 50 characters");
        }
        if (request.getSender().length() > 40) {
            throw new IllegalArgumentException("OCI: Sender email must not exceed 40 characters");
        }
        if (request.getSubject().length() > 100) {
            throw new IllegalArgumentException("OCI: Subject must not exceed 100 characters");
        }
        if (request.getContent().length() > 250) {
            throw new IllegalArgumentException("OCI: Content must not exceed 250 characters");
        }
    }
}
