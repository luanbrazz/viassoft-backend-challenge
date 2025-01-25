package com.viassoft.emailservice.service;

import com.viassoft.emailservice.dto.EmailRequestDTO;
import com.viassoft.emailservice.entity.EmailAudit;
import com.viassoft.emailservice.entity.enums.EmailStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmailService {
    void processEmail(EmailRequestDTO request);

    Page<EmailAudit> findEmailsByStatus(EmailStatus status, Pageable pageable);
}
