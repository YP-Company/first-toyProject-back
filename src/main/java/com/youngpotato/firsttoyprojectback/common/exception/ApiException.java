package com.youngpotato.firsttoyprojectback.common.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final ErrorMessage errorMessage;
    private final String detailMessage;

    public ApiException(String message, ErrorMessage errorMessage, String detailMessage) {
        super(message);
        this.errorMessage = errorMessage;
        this.detailMessage = detailMessage;
    }

    public ApiException(String message, ErrorMessage errorMessage) {
        this(message, errorMessage, "");
    }

    public ApiException(ErrorMessage errorMessage) {
        this(errorMessage.getMessage(), errorMessage, "");
    }

    public ApiException(String message) {
        this(message, ErrorMessage.TEMPORARY_SERVER_ERROR);
    }

    public ErrorMessage getErrorCode() {
        return this.errorMessage;
    }
}
