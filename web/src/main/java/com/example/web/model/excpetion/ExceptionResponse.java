package com.example.web.model.excpetion;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ExceptionResponse(
        Integer businessErrorCode,
        String businessErrorDescription,
        String error,
        Set<String> validationErrors,
        Map<String, String> errors
) {
}
