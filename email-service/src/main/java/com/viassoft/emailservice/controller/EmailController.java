package com.viassoft.emailservice.controller;

import com.viassoft.emailservice.dto.EmailRequestDTO;
import org.springframework.http.ResponseEntity;

public interface EmailController {

    /**
     * Endpoint for sending emails.
     *
     * @param request Email data received in the payload
     * @return ResponseEntity indicating the status of the operation
     */
    ResponseEntity<Void> sendEmail(EmailRequestDTO request);
}