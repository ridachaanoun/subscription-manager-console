package dao;

import java.util.List;
import java.util.Optional;

import entity.Subscription;

public interface SubscriptionDAO {
    void create(Subscription s) throws Exception;
    Optional<Subscription> findById(String id) throws Exception;
    List<Subscription> findAll() throws Exception;
    void update(Subscription s) throws Exception;
    void delete(String id) throws Exception;
    List<Subscription> findActive() throws Exception;
}