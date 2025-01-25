package com.viassoft.emailservice.controller.impl;

import com.viassoft.emailservice.controller.EmailController;
import com.viassoft.emailservice.dto.EmailRequestDTO;
import com.viassoft.emailservice.entity.EmailAudit;
import com.viassoft.emailservice.entity.enums.EmailStatus;
import com.viassoft.emailservice.service.EmailService;
import com.viassoft.emailservice.util.EmailServiceConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
public class EmailControllerImpl implements EmailController {

    private final EmailService emailService;

    @Override
    @PostMapping
    public ResponseEntity<Void> sendEmail(@Valid @RequestBody EmailRequestDTO request) {
        log.info(EmailServiceConstants.PROCESSING_EMAIL_LOG, request);
        emailService.processEmail(request);
        return null;
    }

    @Override
    @GetMapping("/audit")
    public ResponseEntity<Page<EmailAudit>> getEmailsByStatus(
            EmailStatus status,
            @PageableDefault(size = 20, page = 0) Pageable pageable
    ) {
        log.info(EmailServiceConstants.FETCHING_EMAIL_AUDIT_LOGS, status);
        Page<EmailAudit> audits = emailService.findEmailsByStatus(status, pageable);
        return ResponseEntity.ok(audits);
    }
}