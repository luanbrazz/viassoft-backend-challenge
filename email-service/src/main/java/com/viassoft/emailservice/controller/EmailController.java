package com.viassoft.emailservice.controller;

import com.viassoft.emailservice.dto.EmailRequestDTO;
import com.viassoft.emailservice.exception.StandardError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Tag(name = "EmailController", description = "Controller responsible for email operations")
public interface EmailController {

    @Operation(summary = "Send an email",
            description = "Process and send an email based on the integration type (AWS or OCI).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Email processed successfully"),
            @ApiResponse(
                    responseCode = "400", description = "Validation error in the request body",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StandardError.class)
                    )),
            @ApiResponse(
                    responseCode = "500", description = "Internal server error",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StandardError.class)
                    ))
    })
    ResponseEntity<Void> sendEmail(
            @Parameter(description = "Email data received in the payload", required = true)
            EmailRequestDTO request);
}