package dao;

import java.util.List;
import java.util.Optional;

import entity.Payment;

public interface PaymentDAO {
    void create(Payment p) throws Exception;
    Optional<Payment> findById(String id) throws Exception;
    List<Payment> findBySubscription(String subscriptionId) throws Exception;
    List<Payment> findAll() throws Exception;
    void update(Payment p) throws Exception;
    void delete(String id) throws Exception;
    List<Payment> findUnpaidBySubscription(String subscriptionId) throws Exception;
    List<Payment> findLastPayments(int limit) throws Exception;
}