package dev.locker;

import dev.locker.domain.Loan;
import dev.locker.repo.DeviceRepository;
import dev.locker.repo.LoanRepository;
import dev.locker.repo.UserRepository;
import dev.locker.repo.file.FileBackedDeviceRepository;
import dev.locker.repo.file.FileBackedLoanRepository;
import dev.locker.repo.file.FileBackedUserRepository;
import dev.locker.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class LoanServiceTest {
    private LoanService loanService;
    private LoanRepository loanRepo;

    @BeforeEach
    public void setup() {
        Path data = Path.of("data");
        DeviceRepository deviceRepo = new FileBackedDeviceRepository(data.resolve("devices.json"));
        UserRepository userRepo = new FileBackedUserRepository(data.resolve("users.json"));
        loanRepo = new FileBackedLoanRepository(data.resolve("loans.json"));
        loanService = new LoanService(deviceRepo, userRepo, loanRepo);
    }

    @Test
    public void borrowAndReturnHappyPath() {
        Loan loan = loanService.borrow("d-002", "u-103", 3);
        assertEquals("d-002", loan.deviceId());
        assertNotNull(loan.borrowedAt());
        loanService.returnDevice("d-002");
        Optional<Loan> open = loanRepo.findOpenLoanByDevice("d-002");
        assertTrue(open.isEmpty());
    }

    @Test
    public void borrowConflict() {
        assertThrows(RuntimeException.class, () -> loanService.borrow("d-001", "u-103", 2));
    }

    @Test
    public void returnMissing() {
        assertThrows(RuntimeException.class, () -> loanService.returnDevice("non-existent"));
    }
}

