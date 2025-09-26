package serveses.impl;


import dao.PaymentDAO;
import dao.SubscriptionDAO;
import dao.impl.PaymentDAOImpl;
import dao.impl.SubscriptionDAOImpl;
import entity.FixedSubscription;
import entity.Payment;
import entity.Pstatus;
import entity.Subscription;
import serveses.SubscriptionService;
import util.DateUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service implementing subscription business logic.
 */
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionDAO subscriptionDAO = new SubscriptionDAOImpl();
    private final PaymentDAO paymentDAO = new PaymentDAOImpl();

    @Override
    public Subscription create(Subscription s) throws Exception {
        subscriptionDAO.create(s);
        // automatically generate initial monthly payments
        generateMonthlyPaymentsForSubscription(s.getId());
        return s;
    }

    @Override
    public Optional<Subscription> findById(String id) throws Exception {
        return subscriptionDAO.findById(id);
    }

    @Override
    public List<Subscription> findAll() throws Exception {
        return subscriptionDAO.findAll();
    }

    @Override
    public void update(Subscription s) throws Exception {
        subscriptionDAO.update(s);
    }

    @Override
    public void delete(String id) throws Exception {
        subscriptionDAO.delete(id);
    }

    @Override
    public List<Subscription> findActive() throws Exception {
        return subscriptionDAO.findActive();
    }

    @Override
    public void generateMonthlyPaymentsForSubscription(String subscriptionId) throws Exception {
        Optional<Subscription> opt = subscriptionDAO.findById(subscriptionId);
        if (!opt.isPresent()) return;
        Subscription s = opt.get();
        LocalDateTime start = s.getStartDate();
        LocalDateTime end = s.getEndDate() == null ? LocalDateTime.now().plusYears(1) : s.getEndDate();

        List<LocalDateTime> dates = DateUtils.generateMonthlyDates(start, end);

        List<Payment> existing = paymentDAO.findBySubscription(s.getId());
        List<LocalDateTime> existingDue = existing.stream().map(Payment::getDueDate).collect(Collectors.toList());

        int count = 0;
        for (LocalDateTime due : dates) {
            if (existingDue.contains(due)) continue;
            Payment p = new Payment(null, due, null, "auto", Pstatus.UNPAID, s.getId());
            paymentDAO.create(p);
            count++;
            // if fixed subscription limit by monthsEngaged
            if (s instanceof FixedSubscription) {
                int months = ((FixedSubscription) s).getMonthsEngaged();
                if (count >= months) break;
            }
        }
    }
}