package util;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Date helper utilities for monthly schedules and overdue checks.
 * Java 8 compatible (uses java.time.LocalDateTime).
 */
public final class DateUtils {

    private DateUtils() { /* utility class */ }

    /**
     * Generate a list of LocalDateTime monthly occurrences from start (inclusive) to end (inclusive).
     * Uses the same time-of-day and day-of-month as start where possible.
     *
     * If start is null or end is null or start is after end -> returns empty list.
     */
    public static List<LocalDateTime> generateMonthlyDates(LocalDateTime start, LocalDateTime end) {
        List<LocalDateTime> result = new ArrayList<>();
        if (start == null || end == null) return result;
        if (start.isAfter(end)) return result;

        LocalDateTime cur = start;
        while (!cur.isAfter(end)) {
            result.add(cur);
            cur = cur.plusMonths(1);
        }
        return result;
    }

    /**
     * Returns true if the payment is overdue:
     * - if paymentDate is null and now is after dueDate -> overdue
     * - if paymentDate is present and is after dueDate -> overdue
     *
     * Null-safe: if dueDate is null returns false.
     */
    public static boolean isOverdue(LocalDateTime dueDate, LocalDateTime paymentDate) {
        if (dueDate == null) return false;
        if (paymentDate == null) {
            return LocalDateTime.now().isAfter(dueDate);
        }
        return paymentDate.isAfter(dueDate);
    }

    /**
     * Convert LocalDateTime to YearMonth (null-safe).
     */
    public static YearMonth toYearMonth(LocalDateTime dt) {
        return dt == null ? null : YearMonth.from(dt);
    }

    /**
     * Return the next monthly occurrence after the given date (adds one month).
     * Null-safe: returns null if input is null.
     */
    public static LocalDateTime nextMonthly(LocalDateTime date) {
        return date == null ? null : date.plusMonths(1);
    }
}