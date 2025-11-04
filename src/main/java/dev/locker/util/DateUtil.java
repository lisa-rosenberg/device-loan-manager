package dev.locker.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Small date utilities centered around UTC Instant.
 */
@SuppressWarnings("unused")
public final class DateUtil {
    private DateUtil() {
    }

    /**
     * Return the current instant in UTC.
     *
     * @return current Instant in UTC
     */
    public static Instant nowUTC() {
        return Instant.now();
    }

    /**
     * Return an Instant that is {@code days} days offset from {@code base}.
     *
     * @param base the base instant
     * @param days the number of days to add (may be negative)
     * @return new Instant offset by the given number of days
     */
    public static Instant plusDays(Instant base, int days) {
        return base.plus(days, ChronoUnit.DAYS);
    }

    /**
     * Check whether the provided instant falls within the last {@code days} days from now (inclusive).
     *
     * @param instant the instant to check
     * @param days    lookback window in days
     * @return true if the instant is within the last {@code days} days, false otherwise
     */
    public static boolean isWithinLastDays(Instant instant, int days) {
        Instant since = nowUTC().minus(days, ChronoUnit.DAYS);
        return instant.isAfter(since) || instant.equals(since);
    }
}
