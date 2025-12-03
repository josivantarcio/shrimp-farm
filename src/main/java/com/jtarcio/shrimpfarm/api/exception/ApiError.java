package com.jtarcio.shrimpfarm.api.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ApiError {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String path;
    private List<FieldError> fieldErrors;

    @Data
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private String message;
    }

}
