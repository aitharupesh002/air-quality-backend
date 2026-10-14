package com.aqms.backend.exception;

/**
 * Thrown when a request has well-formed syntax but invalid values
 * (for example a latitude outside -90..90). Mapped to HTTP 400.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

}
