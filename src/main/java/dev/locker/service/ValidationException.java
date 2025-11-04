package dev.locker.service;

/**
 * Thrown on invalid input.
 */
public class ValidationException extends RuntimeException {
    /**
     * Create a new ValidationException with the provided message.
     *
     * @param message the error message describing the validation failure
     */
    public ValidationException(String message) {
        super(message);
    }
}
