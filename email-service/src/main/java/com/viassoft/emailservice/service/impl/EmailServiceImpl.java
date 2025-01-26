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
import com.viassoft.emailservice.util.EmailServiceConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
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

    /**
     * Overwrites the mailIntegration value during testing.
     */
    public void setMailIntegrationForTest(String mailIntegration) {
        this.mailIntegration = mailIntegration;
    }

    @Override
    public void processEmail(EmailRequestDTO request) {
        try {
            validateRequest(request);
            Object emailDto = adaptRequestToDto(request);
            logToConsole(emailDto);
            saveAudit(request, EmailStatus.SUCCESS, null, true);
        } catch (Exception ex) {
            log.error(EmailServiceConstants.EMAIL_AUDIT_FAILURE, ex.getMessage());
            saveAudit(request, EmailStatus.FAILURE, ex.getMessage(), false);
            throw new RuntimeException(EmailServiceConstants.PROCESSING_ERROR + ": " + ex.getMessage(), ex);
        }
    }

    @Override
    public Page<EmailAudit> findEmailsByStatus(EmailStatus status, Pageable pageable) {
        return emailAuditRepository.findByStatus(status, pageable);
    }

    /**
     * Adapts the request to the appropriate DTO based on the integration type.
     */
    private Object adaptRequestToDto(EmailRequestDTO request) {
        switch (mailIntegration.toUpperCase()) {
            case "AWS":
                return EmailAwsDTO.builder()
                        .recipient(request.getRecipient())
                        .recipientName(request.getRecipientName())
                        .sender(request.getSender())
                        .subject(request.getSubject())
                        .content(request.getContent())
                        .build();
            case "OCI":
                return EmailOciDTO.builder()
                        .recipientEmail(request.getRecipient())
                        .recipientName(request.getRecipientName())
                        .senderEmail(request.getSender())
                        .subject(request.getSubject())
                        .body(request.getContent())
                        .build();
            default:
                throw new IllegalArgumentException(EmailServiceConstants.INVALID_INTEGRATION_TYPE);
        }
    }

    /**
     * Logs the DTO as a JSON string.
     */
    private void logToConsole(Object dto) throws JsonProcessingException {
        log.info(EmailServiceConstants.EMAIL_AUDIT_SUCCESS, objectMapper.writeValueAsString(dto));
    }

    /**
     * Validates the fields of the request against integration limits.
     */
    private void validateRequest(EmailRequestDTO request) {
        IntegrationLimitsEnum limits = IntegrationLimitsEnum.valueOf(mailIntegration.toUpperCase());
        validateField("recipient", request.getRecipient(), limits.getLimit("recipient"));
        validateField("recipientName", request.getRecipientName(), limits.getLimit("recipientName"));
        validateField("sender", request.getSender(), limits.getLimit("sender"));
        validateField("subject", request.getSubject(), limits.getLimit("subject"));
        validateField("content", request.getContent(), limits.getLimit("content"));
    }

    /**
     * Validates the length of a single field.
     */
    private void validateField(String fieldName, String value, int maxLength) {
        if (value.length() > maxLength) {
            throw new IllegalArgumentException(
                    String.format(EmailServiceConstants.FIELD_EXCEEDS_LIMIT, mailIntegration.toUpperCase(), fieldName, maxLength)
            );
        }
    }

    /**
     * Saves the audit log to the database.
     */
    protected void saveAudit(EmailRequestDTO request, EmailStatus status, String errorMessage, boolean isEmailSent) {
        try {
            if (isEmailSent) validateRequest(request);
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
        } catch (DataIntegrityViolationException ex) {
            log.error(EmailServiceConstants.DATA_INTEGRITY_VIOLATION_WHILE_SAVING_AUDIT, ex.getMessage());
            throw new IllegalArgumentException("Validation failed: " + ex.getMessage(), ex);
        }
    }
}
