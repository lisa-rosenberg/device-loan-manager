package dev.locker.service;

import dev.locker.domain.Device;
import dev.locker.domain.Loan;
import dev.locker.repo.DeviceRepository;
import dev.locker.repo.LoanRepository;
import dev.locker.util.DateUtil;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service that provides simple statistics.
 */
@SuppressWarnings("ClassCanBeRecord")
public class StatsService {
    private final DeviceRepository deviceRepo;
    private final LoanRepository loanRepo;

    /**
     * Create a new StatsService with the given repositories.
     *
     * @param deviceRepo the device repository
     * @param loanRepo   the loan repository
     */
    public StatsService(DeviceRepository deviceRepo, LoanRepository loanRepo) {
        this.deviceRepo = deviceRepo;
        this.loanRepo = loanRepo;
    }

    /**
     * Return top N devices by borrow count since `since` (if null uses last 30 days).
     *
     * @param since the starting instant to consider (inclusive); if null uses 30 days ago
     * @param limit maximum number of entries to return (non-negative)
     * @return list of device/count entries sorted by count descending
     */
    public List<Map.Entry<Device, Long>> popularSince(Instant since, int limit) {
        Instant start = since == null ? DateUtil.plusDays(DateUtil.nowUTC(), -30) : since;
        Map<String, Long> counts = loanRepo.findAll().stream()
                .filter(l -> !l.borrowedAt().isBefore(start))
                .collect(Collectors.groupingBy(Loan::deviceId, Collectors.counting()));

        return deviceRepo.findAll().stream()
                .filter(d -> counts.containsKey(d.id()))
                .map(d -> Map.entry(d, counts.get(d.id())))
                .sorted(Comparator.comparingLong(e -> -e.getValue()))
                .limit(Math.max(0, limit))
                .collect(Collectors.toList());
    }
}
