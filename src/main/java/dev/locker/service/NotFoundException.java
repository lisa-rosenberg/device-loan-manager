package dev.locker.service;

/**
 * Thrown when a domain entity is not found.
 */
public class NotFoundException extends RuntimeException {
    /**
     * Create a new NotFoundException with the provided message.
     *
     * @param message the error message describing what was not found
     */
    public NotFoundException(String message) {
        super(message);
    }
}
