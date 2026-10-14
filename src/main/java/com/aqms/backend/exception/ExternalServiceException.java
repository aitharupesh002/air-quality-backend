package com.aqms.backend.exception;

/**
 * Thrown when a third-party API (air quality / geocoding) fails or returns
 * data we cannot use. Mapped to HTTP 502 Bad Gateway.
 */
public class ExternalServiceException extends RuntimeException {

    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
