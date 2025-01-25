package com.viassoft.emailservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmailRequestDTO {

    @Schema(description = "Recipient's email address", example = "luanbraz2019@gmail.com")
    @Email(message = "Invalid email format for recipient")
    @NotBlank(message = "Recipient email cannot be blank")
    @Size(max = 45, message = "Recipient email must not exceed 45 characters")
    private String recipient;

    @Schema(description = "Recipient's name", example = "Luan Braz")
    @NotBlank(message = "Recipient name cannot be blank")
    @Size(max = 60, message = "Recipient name must not exceed 60 characters")
    private String recipientName;

    @Schema(description = "Sender's email address", example = "luan_silvacpv@hotmail.com")
    @Email(message = "Invalid email format for sender")
    @NotBlank(message = "Sender email cannot be blank")
    @Size(max = 45, message = "Sender email must not exceed 45 characters")
    private String sender;

    @Schema(description = "Email subject", example = "Welcome to our service!")
    @NotBlank(message = "Subject cannot be blank")
    @Size(max = 120, message = "Subject must not exceed 120 characters")
    private String subject;

    @Schema(description = "Email content", example = "Hello, thank you for joining our service!")
    @NotBlank(message = "Content cannot be blank")
    @Size(max = 256, message = "Content must not exceed 256 characters")
    private String content;
}
