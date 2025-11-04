package dev.locker.service;

import dev.locker.domain.Device;
import dev.locker.domain.Loan;
import dev.locker.domain.User;
import dev.locker.repo.DeviceRepository;
import dev.locker.repo.LoanRepository;
import dev.locker.repo.UserRepository;
import dev.locker.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Optional;

/**
 * Handles borrowing and returning devices.
 */
@SuppressWarnings("ClassCanBeRecord")
public class LoanService {
    private static final Logger logger = LoggerFactory.getLogger(LoanService.class);
    private final DeviceRepository deviceRepo;
    private final UserRepository userRepo;
    private final LoanRepository loanRepo;

    /**
     * Create a new LoanService with the given repositories.
     *
     * @param deviceRepo the device repository
     * @param userRepo   the user repository
     * @param loanRepo   the loan repository
     */
    public LoanService(DeviceRepository deviceRepo, UserRepository userRepo, LoanRepository loanRepo) {
        this.deviceRepo = deviceRepo;
        this.userRepo = userRepo;
        this.loanRepo = loanRepo;
    }

    /**
     * Borrow a device for given days. Returns the created Loan.
     * Validates existence, non-negative days, and device not currently borrowed.
     *
     * @param deviceId id of the device to borrow
     * @param userId   id of the user borrowing the device
     * @param days     number of days to borrow (must be positive)
     * @return the created Loan record
     */
    public Loan borrow(String deviceId, String userId, Integer days) {
        if (deviceId == null || deviceId.isBlank()) throw new ValidationException("deviceId is required");
        if (userId == null || userId.isBlank()) throw new ValidationException("userId is required");
        if (days == null || days <= 0) throw new ValidationException("days must be positive");

        Device device = deviceRepo.findById(deviceId).orElseThrow(() -> new NotFoundException("Device not found"));
        User user = userRepo.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        Optional<Loan> open = loanRepo.findOpenLoanByDevice(deviceId);
        if (open.isPresent()) throw new ConflictException("Device already borrowed");

        Instant now = DateUtil.nowUTC();
        Instant due = DateUtil.plusDays(now, days);
        Loan loan = new Loan(deviceId, userId, now, due, null);
        loanRepo.save(loan);
        deviceRepo.save(device.incrementTimesBorrowed());
        logger.info("Device {} borrowed by {} (user: {}) until {}", deviceId, userId, user.name(), due);
        return loan;
    }

    /**
     * Return a device. Returns the updated Loan (with returnedAt set).
     *
     * @param deviceId id of the device to return
     * @return the updated Loan record
     */
    public Loan returnDevice(String deviceId) {
        if (deviceId == null || deviceId.isBlank()) throw new ValidationException("deviceId is required");
        Loan open = loanRepo.findOpenLoanByDevice(deviceId).orElseThrow(() -> new NotFoundException("Open loan not found for device"));
        Loan updated = open.withReturnedAt(DateUtil.nowUTC());
        loanRepo.update(updated);
        logger.info("Device {} returned", deviceId);
        return updated;
    }
}
