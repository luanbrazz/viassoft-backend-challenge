package com.viassoft.emailservice.entity.enums;

import java.util.Map;

public enum IntegrationLimitsEnum {
    AWS(Map.of(
            "recipient", 45,
            "recipientName", 60,
            "sender", 45,
            "subject", 120,
            "content", 256
    )),
    OCI(Map.of(
            "recipient", 40,
            "recipientName", 50,
            "sender", 40,
            "subject", 100,
            "content", 250
    ));

    private final Map<String, Integer> fieldLimits;

    IntegrationLimitsEnum(Map<String, Integer> fieldLimits) {
        this.fieldLimits = fieldLimits;
    }

    public int getLimit(String field) {
        return fieldLimits.getOrDefault(field, Integer.MAX_VALUE);
    }
}