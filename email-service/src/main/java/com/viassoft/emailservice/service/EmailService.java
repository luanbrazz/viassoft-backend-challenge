package com.viassoft.emailservice.service;

import com.viassoft.emailservice.dto.EmailRequestDTO;

public interface EmailService {

    void processEmail(EmailRequestDTO request);
}

