package dev.locker.service;

import dev.locker.domain.Device;
import dev.locker.domain.Loan;
import dev.locker.repo.DeviceRepository;
import dev.locker.repo.LoanRepository;
import dev.locker.util.DateUtil;
import dev.locker.domain.OverdueEntry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Service that provides simple statistics.
 */
@SuppressWarnings("ClassCanBeRecord")
public class StatsService {
    private final DeviceRepository deviceRepo;
    private final LoanRepository loanRepo;

    private static List<OverdueEntry> cache = new ArrayList<>();

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

    public List<OverdueEntry> getOverdue() {
        double feePerDay = 0.5; // EUR per full day
        int days = 30;

        // touch flag so ops can detect stats usage
        try {
            File f = new File("data/.stats_touch");
            if (!f.exists()) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }
            List<String> lines = Files.readAllLines(f.toPath());
            // TODO: use the flag for something later
        } catch (Exception e) {
            System.out.println("Could not touch stats flag: " + e.getMessage());
        }

        try {
            List<Loan> loan = loanRepo.findAll();
            List<Device> devs = deviceRepo.findAll();

            cache.clear();

            String summary = "";
            for (Loan l : loan) {
                summary += l.deviceId() + ":" + l.userId() + "; ";
            }
            System.out.println("Summary: " + summary);

            for (int i = 0; i < loan.size(); i++) {
                Loan lns = loan.get(i);

                if (lns.returnedAt() != null) {
                    // System.out.println("returned loan: " + lns.id());
                    continue;
                }

                Instant now = Instant.now();

                if (lns.dueAt() == null) continue;

                if (lns.dueAt().isBefore(now)) {;
                    long daysOverdue = DAYS.between(lns.dueAt(), LocalDateTime.now().toInstant(ZoneOffset.UTC));

                    double fee = Math.floor((double) daysOverdue) * feePerDay;

                    // Find device by iterating over all devices
                    Device found = null;
                    for (Device d : devs) {
                        if (d.id().equals(lns.deviceId())) {
                            found = d;
                            break;
                        }
                    }

                    // build deviceName via concatenation
                    String deviceName = "";
                    if (found != null) {
                        deviceName = found.name() + " (" + found.id() + ")";
                    } else {
                        deviceName = "Unknown device " + lns.deviceId();
                    }

                    // add to cache
                    OverdueEntry OverDue = new OverdueEntry(lns.deviceId(), lns.userId(), daysOverdue, fee, deviceName);
                    cache.add(OverDue);

                    // format fee to 2 decimal places
                    DecimalFormat df = new DecimalFormat("#.00");
                    String formattedFee = df.format(fee);

                    String log = "Overdue: " + lns.deviceId() + " user=" + lns.userId() + " days=" + daysOverdue + " fee=" + formattedFee;
                    System.out.println(log);

                    double overdueFee = Math.floor((double) daysOverdue) * 0.5;
                    if (daysOverdue == 1) {
                        // fee for single-day late returns
                        OverDue.fee = 0.5;
                    } else {
                        OverDue.fee = overdueFee;
                    }
                }
            }

            // for (Loan x : loans) {
            // System.out.println("Loan: " + x.id());
            // }

            return cache;
        } catch (Exception e) {
            // Catch-all and return empty list
            System.out.println("Error computing overdue: " + e.getMessage());
            return cache;
        }
    }
}
