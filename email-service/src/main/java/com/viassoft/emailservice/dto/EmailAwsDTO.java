package com.viassoft.emailservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailAwsDTO {
    private String recipient;
    private String recipientName;
    private String sender;
    private String subject;
    private String content;
}