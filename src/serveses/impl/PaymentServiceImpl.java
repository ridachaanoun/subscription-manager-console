package serveses.impl;


import dao.PaymentDAO;
import dao.SubscriptionDAO;
import dao.impl.PaymentDAOImpl;
import dao.impl.SubscriptionDAOImpl;
import entity.Payment;
import entity.Pstatus;
import entity.Subscription;
import serveses.PaymentService;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

/**
 * Payment service implementing business logic.
 */
public class PaymentServiceImpl implements PaymentService {

    private final PaymentDAO paymentDAO = new PaymentDAOImpl();
    private final SubscriptionDAO subscriptionDAO = new SubscriptionDAOImpl();

    @Override
    public Payment recordPayment(Payment p) throws Exception {
        // If paymentDate provided, set status PAID, else UNPAID
        if (p.getPaymentDate() != null) p.setStatus(Pstatus.PAID);
        else p.setStatus(Pstatus.UNPAID);
        // Try to update existing payment if ID exists, otherwise create
        if (p.getId() == null || p.getId().trim().isEmpty()) {
            paymentDAO.create(p);
        } else {
            // if exists, update, else create
            Optional<Payment> opt = paymentDAO.findById(p.getId());
            if (opt.isPresent()) paymentDAO.update(p); else paymentDAO.create(p);
        }
        return p;
    }

    @Override
    public Optional<Payment> findById(String id) throws Exception {
        return paymentDAO.findById(id);
    }

    @Override
    public List<Payment> findBySubscription(String subscriptionId) throws Exception {
        return paymentDAO.findBySubscription(subscriptionId);
    }

    @Override
    public List<Payment> findUnpaidBySubscription(String subscriptionId) throws Exception {
        return paymentDAO.findUnpaidBySubscription(subscriptionId);
    }

    @Override
    public List<Payment> findLastPayments(int limit) throws Exception {
        return paymentDAO.findLastPayments(limit);
    }

    @Override
    public double totalPaidForSubscription(String subscriptionId) throws Exception {
        Optional<Subscription> opt = subscriptionDAO.findById(subscriptionId);
        if (!opt.isPresent()) return 0.0;
        double price = opt.get().getPrice();
        return paymentDAO.findBySubscription(subscriptionId).stream()
                .filter(p -> p.getStatus() == Pstatus.PAID)
                .count() * price;
    }

    @Override
    public double totalUnpaidForSubscription(String subscriptionId) throws Exception {
        Optional<Subscription> opt = subscriptionDAO.findById(subscriptionId);
        if (!opt.isPresent()) return 0.0;
        double price = opt.get().getPrice();
        return paymentDAO.findUnpaidBySubscription(subscriptionId).stream()
                .count() * price;
    }

    @Override
    public double totalPaidForMonth(YearMonth month) throws Exception {
        return paymentDAO.findAll().stream()
                .filter(p -> p.getPaymentDate() != null && YearMonth.from(p.getPaymentDate()).equals(month))
                .mapToDouble(p -> {
                    try {
                        Optional<Subscription> s = subscriptionDAO.findById(p.getSubscriptionId());
                        return s.map(Subscription::getPrice).orElse(0.0);
                    } catch (Exception e) {
                        return 0.0;
                    }
                })
                .sum();
    }

    @Override
    public double totalPaidForYear(int year) throws Exception {
        return paymentDAO.findAll().stream()
                .filter(p -> p.getPaymentDate() != null && p.getPaymentDate().getYear() == year)
                .mapToDouble(p -> {
                    try {
                        Optional<Subscription> s = subscriptionDAO.findById(p.getSubscriptionId());
                        return s.map(Subscription::getPrice).orElse(0.0);
                    } catch (Exception e) {
                        return 0.0;
                    }
                })
                .sum();
    }

    @Override
    public void markPaymentAsPaid(String paymentId) throws Exception {
        Optional<Payment> opt = paymentDAO.findById(paymentId);
        if (!opt.isPresent()) throw new IllegalArgumentException("Payment not found: " + paymentId);
        Payment p = opt.get();
        p.setPaymentDate(java.time.LocalDateTime.now());
        p.setStatus(Pstatus.PAID);
        paymentDAO.update(p);
    }

    @Override
    public void delete(String paymentId) throws Exception {
        Optional<Payment> opt = paymentDAO.findById(paymentId);
        if (!opt.isPresent()) throw new IllegalArgumentException("Payment not found: " + paymentId);
        paymentDAO.delete(paymentId);
    }
}
