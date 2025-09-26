package serveses;


import entity.Payment;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface PaymentService {
    Payment recordPayment(Payment p) throws Exception;
    Optional<Payment> findById(String id) throws Exception;
    List<Payment> findBySubscription(String subscriptionId) throws Exception;
    List<Payment> findUnpaidBySubscription(String subscriptionId) throws Exception;
    List<Payment> findLastPayments(int limit) throws Exception;

    double totalPaidForSubscription(String subscriptionId) throws Exception;
    double totalUnpaidForSubscription(String subscriptionId) throws Exception;
    double totalPaidForMonth(YearMonth month) throws Exception;
    double totalPaidForYear(int year) throws Exception;

    void markPaymentAsPaid(String paymentId) throws Exception;
}