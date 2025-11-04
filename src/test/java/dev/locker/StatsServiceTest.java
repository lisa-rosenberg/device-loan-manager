package dev.locker;

import dev.locker.domain.Device;
import dev.locker.repo.DeviceRepository;
import dev.locker.repo.LoanRepository;
import dev.locker.repo.file.FileBackedDeviceRepository;
import dev.locker.repo.file.FileBackedLoanRepository;
import dev.locker.service.StatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StatsServiceTest {
    private StatsService statsService;

    @BeforeEach
    public void setup() {
        Path data = Path.of("data");
        DeviceRepository deviceRepo = new FileBackedDeviceRepository(data.resolve("devices.json"));
        LoanRepository loanRepo = new FileBackedLoanRepository(data.resolve("loans.json"));
        statsService = new StatsService(deviceRepo, loanRepo);
    }

    @Test
    public void popularSinceLast30Days() {
        List<Map.Entry<Device, Long>> top = statsService.popularSince(null, 5);
        assertNotNull(top);
    }
}

