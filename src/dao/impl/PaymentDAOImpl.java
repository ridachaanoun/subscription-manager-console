package dao.impl;



import dao.PaymentDAO;
import entity.*;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class PaymentDAOImpl implements PaymentDAO {

    @Override
    public void create(Payment p) throws Exception {
        String sql = "INSERT INTO payment(id, subscription_id, due_date, payment_date, payment_type, status) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getId());
            ps.setString(2, p.getSubscriptionId());
            ps.setTimestamp(3, Timestamp.valueOf(p.getDueDate()));
            if (p.getPaymentDate() != null) ps.setTimestamp(4, Timestamp.valueOf(p.getPaymentDate())); else ps.setNull(4, Types.TIMESTAMP);
            ps.setString(5, p.getPaymentType());
            ps.setString(6, p.getStatus().name());
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Payment> findById(String id) throws Exception {
        String sql = "SELECT * FROM payment WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Payment> findBySubscription(String subscriptionId) throws Exception {
        String sql = "SELECT * FROM payment WHERE subscription_id = ? ORDER BY due_date DESC";
        List<Payment> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, subscriptionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public List<Payment> findAll() throws Exception {
        String sql = "SELECT * FROM payment ORDER BY due_date DESC";
        List<Payment> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public void update(Payment p) throws Exception {
        String sql = "UPDATE payment SET payment_date=?, payment_type=?, status=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (p.getPaymentDate() != null) ps.setTimestamp(1, Timestamp.valueOf(p.getPaymentDate())); else ps.setNull(1, Types.TIMESTAMP);
            ps.setString(2, p.getPaymentType());
            ps.setString(3, p.getStatus().name());
            ps.setString(4, p.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(String id) throws Exception {
        String sql = "DELETE FROM payment WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Payment> findUnpaidBySubscription(String subscriptionId) throws Exception {
        String sql = "SELECT * FROM payment WHERE subscription_id = ? AND status <> ?";
        List<Payment> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, subscriptionId);
            ps.setString(2, Pstatus.PAID.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public List<Payment> findLastPayments(int limit) throws Exception {
        String sql = "SELECT * FROM payment ORDER BY CASE WHEN payment_date IS NULL THEN 1 ELSE 0 END, payment_date DESC LIMIT ?";
        List<Payment> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    private Payment mapRow(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String subscriptionId = rs.getString("subscription_id");
        Timestamp tsDue = rs.getTimestamp("due_date");
        Timestamp tsPay = rs.getTimestamp("payment_date");
        String type = rs.getString("payment_type");
        String statusStr = rs.getString("status");
        LocalDateTime due = tsDue.toLocalDateTime();
        LocalDateTime pay = tsPay == null ? null : tsPay.toLocalDateTime();
        return new Payment(id, due, pay, type, Pstatus.valueOf(statusStr), subscriptionId);
    }
}