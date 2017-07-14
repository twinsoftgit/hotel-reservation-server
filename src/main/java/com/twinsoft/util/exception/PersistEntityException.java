package com.twinsoft.util.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception to be thrown when there is an error persisting an entity.
 *
 * @author Miodrag Pavkovic
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class PersistEntityException extends RuntimeException {

    private static final long serialVersionUID = -3179072851269263468L;

    private static final String DEFAULT_MESSAGE = "Entity could not be persisted into respository";

    /**
     * Default constructor
     */
    public PersistEntityException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Parameterized constructor with custom message
     *
     * @param message custom error message
     */
    public PersistEntityException(final String message) {
        super(message);
    }

    /**
     * Parameterized constructor with custom cause
     *
     * @param cause cause for exception
     */
    public PersistEntityException(final Throwable cause) {
        super(cause);
    }

    /**
     * Parameterized constructor with custom message and cause
     *
     * @param message custom error message
     * @param cause cause for exception
     */
    public PersistEntityException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
