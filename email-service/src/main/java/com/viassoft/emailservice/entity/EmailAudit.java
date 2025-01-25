package com.viassoft.emailservice.entity;

import com.viassoft.emailservice.entity.enums.EmailStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "email_audit")
public class EmailAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 45)
    private String recipient;

    @Column(nullable = false, length = 60)
    private String recipientName;

    @Column(nullable = false, length = 45)
    private String sender;

    @Column(nullable = false, length = 120)
    private String subject;

    @Column(nullable = false, length = 256)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EmailStatus status;

    @Column(length = 500)
    private String errorMessage;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false, length = 10)
    private String integrationType;
}
