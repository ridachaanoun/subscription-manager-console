package util;

import entity.Payment;
import entity.Pstatus;
import entity.Subscription;

import java.util.Objects;

/**
 * Simple validation helpers used by the UI and services.
 * Throw IllegalArgumentException when validation fails.
 */
public final class ValidationUtils {

    private ValidationUtils() { /* utility class */ }

    /**
     * Validate a Subscription object.
     * - not null
     * - serviceName not empty
     * - price >= 0
     * - startDate not null
     * - if endDate present it must not be before startDate
     */
    public static void validateSubscription(Subscription s) {
        Objects.requireNonNull(s, "subscription must not be null");
        if (s.getServiceName() == null || s.getServiceName().trim().isEmpty()) {
            throw new IllegalArgumentException("serviceName is required");
        }
        if (s.getPrice() < 0) {
            throw new IllegalArgumentException("price must be >= 0");
        }
        if (s.getStartDate() == null) {
            throw new IllegalArgumentException("startDate is required");
        }
        if (s.getEndDate() != null && s.getEndDate().isBefore(s.getStartDate())) {
            throw new IllegalArgumentException("endDate must be after or equal to startDate");
        }
    }

    /**
     * Validate a Payment object.
     * - not null
     * - subscriptionId present
     * - dueDate present
     * - status present (Pstatus)
     * - if paymentDate present it must not be before dueDate (optional business rule)
     */
    public static void validatePayment(Payment p) {
        Objects.requireNonNull(p, "payment must not be null");
        if (p.getSubscriptionId() == null || p.getSubscriptionId().trim().isEmpty()) {
            throw new IllegalArgumentException("subscriptionId is required for payment");
        }
        if (p.getDueDate() == null) {
            throw new IllegalArgumentException("dueDate is required");
        }
        if (p.getStatus() == null) {
            throw new IllegalArgumentException("payment status is required");
        }
        // Optional: disallow paymentDate before dueDate (change if your business allows early payments)
        if (p.getPaymentDate() != null && p.getPaymentDate().isBefore(p.getDueDate())) {
            throw new IllegalArgumentException("paymentDate cannot be before dueDate");
        }
        // Ensure status consistency: if paymentDate is set, status should be PAID (best-effort)
        if (p.getPaymentDate() != null && p.getStatus() != Pstatus.PAID) {
            throw new IllegalArgumentException("payment with paymentDate must have status PAID");
        }
    }
}