package dev.locker.service;

/**
 * Thrown when an operation cannot be completed due to a conflict (e.g. device already borrowed).
 */
public class ConflictException extends RuntimeException {
    /**
     * Create a new ConflictException with the provided message.
     *
     * @param message the error message describing the conflict
     */
    public ConflictException(String message) {
        super(message);
    }
}
