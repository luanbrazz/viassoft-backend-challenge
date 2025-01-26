package com.viassoft.emailservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viassoft.emailservice.dto.EmailRequestDTO;
import com.viassoft.emailservice.entity.EmailAudit;
import com.viassoft.emailservice.entity.enums.EmailStatus;
import com.viassoft.emailservice.repository.EmailAuditRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceImplTest {

    @Mock
    private EmailAuditRepository emailAuditRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processEmailSuccessAws() throws JsonProcessingException {
        // Given: Configura os dados de entrada para o teste.
        EmailRequestDTO request = new EmailRequestDTO();
        request.setRecipient("recipient@test.com");
        request.setRecipientName("Test User");
        request.setSender("sender@test.com");
        request.setSubject("Subject");
        request.setContent("Content");

        // Define o tipo de integração como AWS, usando Reflection para alterar o campo privado.
        ReflectionTestUtils.setField(emailService, "mailIntegration", "AWS");

        // Simula a serialização do objeto para JSON.
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"key\":\"value\"}");

        // When: Chama o método de processamento de email.
        emailService.processEmail(request);

        // Then: Verifica se o audit log foi salvo corretamente.
        ArgumentCaptor<EmailAudit> auditCaptor = ArgumentCaptor.forClass(EmailAudit.class); // Captura o objeto salvo.
        verify(emailAuditRepository, times(1)).save(auditCaptor.capture()); // Verifica que o repositório foi chamado uma vez.

        EmailAudit savedAudit = auditCaptor.getValue(); // Obtém o audit log salvo.
        assertEquals(EmailStatus.SUCCESS, savedAudit.getStatus()); // Verifica se o status é SUCCESS.
        assertNull(savedAudit.getErrorMessage()); // Garante que nenhuma mensagem de erro foi salva.
    }

    @Test
    void findEmailsByStatusSuccess() {
        // Given: Configura o status e a paginação simulados.
        EmailStatus status = EmailStatus.SUCCESS; // Status para o filtro.
        Pageable pageable = Pageable.unpaged(); // Configura a paginação.
        Page<EmailAudit> page = new PageImpl<>(Collections.emptyList()); // Página vazia como resposta simulada.

        when(emailAuditRepository.findByStatus(status, pageable)).thenReturn(page); // Simula o comportamento do repositório.

        // When: Chama o método do serviço para buscar os emails pelo status.
        Page<EmailAudit> result = emailService.findEmailsByStatus(status, pageable);

        // Then: Verifica se o resultado está correto.
        assertNotNull(result); // Garante que o resultado não é nulo.
        verify(emailAuditRepository, times(1)).findByStatus(status, pageable); // Verifica que o repositório foi chamado uma vez.
    }
}
