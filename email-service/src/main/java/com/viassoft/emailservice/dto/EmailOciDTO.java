package com.viassoft.emailservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailOciDTO {
    private String recipientEmail;
    private String recipientName;
    private String senderEmail;
    private String subject;
    private String body;
}