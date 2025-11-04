package dev.locker.repo;

import dev.locker.domain.Loan;

import java.util.List;
import java.util.Optional;

/**
 * Repository for loans.
 */
public interface LoanRepository {
    /**
     * Return all loans (historic and open).
     *
     * @return an unmodifiable list of loans
     */
    List<Loan> findAll();

    /**
     * Find an open (not returned) loan for the given device.
     *
     * @param deviceId the device id to search for
     * @return an Optional with the open loan if present
     */
    Optional<Loan> findOpenLoanByDevice(String deviceId);

    /**
     * Persist a new loan.
     *
     * @param loan the loan to save
     */
    void save(Loan loan);

    /**
     * Update an existing loan record.
     *
     * @param loan the loan to update
     */
    void update(Loan loan);
}
