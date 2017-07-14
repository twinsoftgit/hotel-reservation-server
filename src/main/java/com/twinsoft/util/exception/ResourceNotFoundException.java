package com.twinsoft.util.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom ResourceNotFoundException - replacement for springframework ResourceNotFoundException
 *
 * @author Miodrag Pavkovic
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 5075331785718564755L;

    private static final String DEFAULT_MESSAGE = "Resource not found";

    /**
     * Default constructor
     */
    public ResourceNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Parameterized constructor with custom message
     *
     * @param message custom error message
     */
    public ResourceNotFoundException(final String message) {
        super(message);
    }

    /**
     * Parameterized constructor with custom cause
     *
     * @param cause cause for exception
     */
    public ResourceNotFoundException(final Throwable cause) {
        super(cause);
    }

    /**
     * Parameterized constructor with custom message and cause
     *
     * @param message custom error message
     * @param cause cause for exception
     */
    public ResourceNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
