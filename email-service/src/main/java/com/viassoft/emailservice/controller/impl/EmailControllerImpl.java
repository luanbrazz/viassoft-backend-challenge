package com.viassoft.emailservice.controller.impl;

import com.viassoft.emailservice.controller.EmailController;
import com.viassoft.emailservice.dto.EmailRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
public class EmailControllerImpl implements EmailController {

    @Override
    @PostMapping
    public ResponseEntity<Void> sendEmail(@Valid @RequestBody EmailRequestDTO request) {
        log.info("Sending email: {}", request);

        return null;
    }
}