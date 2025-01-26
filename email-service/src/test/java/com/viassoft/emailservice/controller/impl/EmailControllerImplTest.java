package com.viassoft.emailservice.controller.impl;

import com.viassoft.emailservice.dto.EmailRequestDTO;
import com.viassoft.emailservice.entity.EmailAudit;
import com.viassoft.emailservice.entity.enums.EmailStatus;
import com.viassoft.emailservice.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailControllerImplTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailControllerImpl emailController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa os mocks antes de cada teste.
    }

    @Test
    void sendEmailSuccess() {
        // Given:
        EmailRequestDTO request = new EmailRequestDTO();
        request.setRecipient("recipient@gmail.com");
        request.setRecipientName("Recipient Name");
        request.setSender("sender@gmail.com");
        request.setSubject("Subject");
        request.setContent("Content");

        // When:
        ResponseEntity<Void> response = emailController.sendEmail(request);

        // Then:
        assertNotNull(response);
        assertEquals(204, response.getStatusCodeValue());
        verify(emailService, times(1)).processEmail(request);
    }

    @Test
    void getEmailsByStatusSuccess() {
        // Given:
        EmailStatus status = EmailStatus.SUCCESS;
        Pageable pageable = Pageable.unpaged(); // Simula a paginação.
        Page<EmailAudit> page = new PageImpl<>(Collections.emptyList()); // Página vazia como resposta simulada.

        when(emailService.findEmailsByStatus(status, pageable)).thenReturn(page); // Simula o comportamento do serviço.

        // When:
        ResponseEntity<Page<EmailAudit>> response = emailController.getEmailsByStatus(status, pageable);

        // Then:
        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        verify(emailService, times(1)).findEmailsByStatus(status, pageable);
    }
}
