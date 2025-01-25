package com.viassoft.emailservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viassoft.emailservice.dto.EmailAwsDTO;
import com.viassoft.emailservice.dto.EmailOciDTO;
import com.viassoft.emailservice.dto.EmailRequestDTO;
import com.viassoft.emailservice.entity.EmailAudit;
import com.viassoft.emailservice.entity.enums.EmailStatus;
import com.viassoft.emailservice.entity.enums.IntegrationLimitsEnum;
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
            this.validateRequest(request, mailIntegration);

            if ("AWS".equalsIgnoreCase(mailIntegration)) {
                EmailAwsDTO awsDTO = adaptToAws(request);
                this.logToConsole(awsDTO);
                this.saveAudit(request, EmailStatus.SUCCESS, null);
            } else if ("OCI".equalsIgnoreCase(mailIntegration)) {
                EmailOciDTO ociDTO = adaptToOci(request);
                this.logToConsole(ociDTO);
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

    private void validateRequest(EmailRequestDTO request, String integrationType) {
        IntegrationLimitsEnum limits = IntegrationLimitsEnum.valueOf(integrationType.toUpperCase());
        checkFieldLength("recipient", request.getRecipient(), limits.getLimit("recipient"));
        checkFieldLength("recipientName", request.getRecipientName(), limits.getLimit("recipientName"));
        checkFieldLength("sender", request.getSender(), limits.getLimit("sender"));
        checkFieldLength("subject", request.getSubject(), limits.getLimit("subject"));
        checkFieldLength("content", request.getContent(), limits.getLimit("content"));
    }

    private void checkFieldLength(String fieldName, String value, int maxLength) {
        if (value.length() > maxLength) {
            throw new IllegalArgumentException(
                    String.format("%s: %s must not exceed %d characters", mailIntegration.toUpperCase(), fieldName, maxLength)
            );
        }
    }

}
