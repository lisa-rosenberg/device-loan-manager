package dev.locker.repo;

import dev.locker.domain.User;

import java.util.List;
import java.util.Optional;

/**
 * Repository for users.
 */
public interface UserRepository {
    /**
     * Return all users stored in the repository.
     *
     * @return an unmodifiable list of users
     */
    List<User> findAll();

    /**
     * Find a user by id.
     *
     * @param id the user id
     * @return an Optional containing the user if found
     */
    Optional<User> findById(String id);
}
