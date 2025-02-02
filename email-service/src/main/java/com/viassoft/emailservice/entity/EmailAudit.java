package com.viassoft.emailservice.entity;

import com.viassoft.emailservice.entity.enums.EmailStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "email_audit")
public class EmailAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 256)
    private String recipient;

    @Column(nullable = false, length = 256)
    private String recipientName;

    @Column(nullable = false, length = 256)
    private String sender;

    @Column(nullable = false, length = 256)
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

    @Column(nullable = false, length = 20)
    private String integrationType;
}
