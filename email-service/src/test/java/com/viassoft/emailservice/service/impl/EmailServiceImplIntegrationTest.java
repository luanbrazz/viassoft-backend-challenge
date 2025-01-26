package com.viassoft.emailservice.service.impl;

import com.viassoft.emailservice.dto.EmailRequestDTO;
import com.viassoft.emailservice.entity.EmailAudit;
import com.viassoft.emailservice.entity.enums.EmailStatus;
import com.viassoft.emailservice.repository.EmailAuditRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Garante que o contexto será limpo após cada teste.
class EmailServiceImplIntegrationTest {

    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private EmailAuditRepository emailAuditRepository;

    @Test
    @Transactional
    void testAuditSaveSuccess() {
        // Given:
        EmailRequestDTO request = new EmailRequestDTO();
        request.setRecipient("recipient@gmail.com");
        request.setRecipientName("Recipient Name");
        request.setSender("sender@gmail.com");
        request.setSubject("Test Subject");
        request.setContent("This is a test content.");

        // integração AWS/oci
        emailService.setMailIntegrationForTest("AWS");

        // When:
        emailService.processEmail(request);

        // Then:
        List<EmailAudit> audits = emailAuditRepository.findAll();
        assertEquals(1, audits.size());

        EmailAudit audit = audits.get(0);
        assertEquals("recipient@gmail.com", audit.getRecipient());
        assertEquals("Recipient Name", audit.getRecipientName());
        assertEquals("sender@gmail.com", audit.getSender());
        assertEquals("Test Subject", audit.getSubject());
        assertEquals("This is a test content.", audit.getContent());
        assertEquals(EmailStatus.SUCCESS, audit.getStatus());
        assertNull(audit.getErrorMessage());
    }

    @Test
    @Transactional
    void testAuditSaveFailure() {
        // Given:
        EmailRequestDTO request = new EmailRequestDTO();
        request.setRecipient("thisisaverylongemailaddressbeyondthelimitallowed@example.com");
        request.setRecipientName("Test User");
        request.setSender("sender@gmail.com");
        request.setSubject("Test Subject");
        request.setContent("This is a test content.");

        // Define o tipo de integração como AWS para o teste.
        emailService.setMailIntegrationForTest("AWS");

        // When & Then:
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailService.processEmail(request);
        });

        // Valida a mensagem de erro da exceção
        assertTrue(exception.getMessage().contains("recipient must not exceed"));


        List<EmailAudit> audits = emailAuditRepository.findAll();
        assertEquals(1, audits.size());
    }

}
