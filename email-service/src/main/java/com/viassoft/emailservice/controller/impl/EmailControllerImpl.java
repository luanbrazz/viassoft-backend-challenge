package com.viassoft.emailservice.controller.impl;

import com.viassoft.emailservice.controller.EmailController;
import com.viassoft.emailservice.dto.EmailRequestDTO;
import com.viassoft.emailservice.entity.EmailAudit;
import com.viassoft.emailservice.entity.enums.EmailStatus;
import com.viassoft.emailservice.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        log.info("Processing email: {}", request);
        emailService.processEmail(request);
        return null;
    }

    @Override
    @GetMapping("/audit")
    public ResponseEntity<Page<EmailAudit>> getEmailsByStatus(EmailStatus status, Pageable pageable) {
        log.info("Fetching email audit logs with status: {}", status);
        Page<EmailAudit> audits = emailService.findEmailsByStatus(status, pageable);
        return ResponseEntity.ok(audits);
    }
}