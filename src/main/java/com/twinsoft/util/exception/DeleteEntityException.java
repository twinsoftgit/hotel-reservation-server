package com.twinsoft.util.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception to be thrown when deleting of entity is not possible.
 *
 * @author Miodrag Pavkovic
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DeleteEntityException extends RuntimeException {

    private static final long serialVersionUID = -7859947517051354651L;

    private static final String DEFAULT_MESSAGE = "Entity could not be deleted";

    /**
     * Default constructor
     */
    public DeleteEntityException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Parameterized constructor with custom message
     *
     * @param message custom error message
     */
    public DeleteEntityException(final String message) {
        super(message);
    }

    /**
     * Parameterized constructor with custom cause
     *
     * @param cause cause for exception
     */
    public DeleteEntityException(final Throwable cause) {
        super(cause);
    }

    /**
     * Parameterized constructor with custom message and cause
     *
     * @param message custom error message
     * @param cause cause for exception
     */
    public DeleteEntityException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
