package com.viassoft.emailservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EmailServiceConstants {

    public static final String PROCESSING_EMAIL_LOG = "Processing email: {}";
    public static final String FETCHING_EMAIL_AUDIT_LOGS = "Fetching email audit logs with status: {}";

    public static final String FIELD_EXCEEDS_LIMIT = "%s: %s must not exceed %d characters";

    public static final String INVALID_INTEGRATION_TYPE = "Invalid mail integration configuration";

    public static final String VALIDATION_ERROR = "Validation Error";
    public static final String PROCESSING_ERROR = "Processing Error";
    public static final String INVALID_ARGUMENT = "Invalid Argument";
    public static final String RUNTIME_EXCEPTION = "Runtime Exception";
    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";

    public static final String EMAIL_AUDIT_SUCCESS = "Email successfully processed: {}";
    public static final String EMAIL_AUDIT_FAILURE = "Email processing failed";
}
