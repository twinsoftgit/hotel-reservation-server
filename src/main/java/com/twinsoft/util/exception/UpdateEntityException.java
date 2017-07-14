package com.twinsoft.util.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception to be thrown when exception occurs while updating existing entity in repository.
 *
 * @author Miodrag Pavkovic
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class UpdateEntityException extends RuntimeException {

    private static final long serialVersionUID = -8578985654581195872L;

    private static final String DEFAULT_MESSAGE = "Entity could not be updated";

    /**
     * Default constructor
     */
    public UpdateEntityException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Parameterized constructor with custom message
     *
     * @param message custom error message
     */
    public UpdateEntityException(final String message) {
        super(message);
    }

    /**
     * Parameterized constructor with custom cause
     *
     * @param cause cause for exception
     */
    public UpdateEntityException(final Throwable cause) {
        super(cause);
    }

    /**
     * Parameterized constructor with custom message and cause
     *
     * @param message custom error message
     * @param cause cause for exception
     */
    public UpdateEntityException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
