package com.viassoft.emailservice.controller;

import com.viassoft.emailservice.dto.EmailRequestDTO;
import com.viassoft.emailservice.entity.EmailAudit;
import com.viassoft.emailservice.entity.enums.EmailStatus;
import com.viassoft.emailservice.exception.StandardError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@Tag(name = "EmailController", description = "Controller responsible for email operations")
public interface EmailController {

    @Operation(summary = "Send an email",
            description = "Process and send an email based on the integration type (AWS or OCI).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Email processed successfully"),
            @ApiResponse(
                    responseCode = "400", description = "Validation error in the request body",
                    content = @Content(schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(
                    responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = StandardError.class)))
    })
    ResponseEntity<Void> sendEmail(EmailRequestDTO request);

    @Operation(summary = "Get paginated email audit by status",
            description = "Fetch paginated audit logs of emails filtered by their status (SUCCESS or FAILURE).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paginated audit logs found",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmailAudit.class)))),
            @ApiResponse(
                    responseCode = "400", description = "Invalid status provided",
                    content = @Content(schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(
                    responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = StandardError.class)))
    })
    ResponseEntity<Page<EmailAudit>> getEmailsByStatus(
            @Parameter(description = "Email status to filter by (SUCCESS or FAILURE)",
                    example = "FAILURE", required = true)
            EmailStatus status,
            @Parameter(description = "Page number to fetch", example = "0", required = false)
            Pageable pageable
    );
}