package dao.impl;


import dao.SubscriptionDAO;
import entity.*;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class SubscriptionDAOImpl implements SubscriptionDAO {

    @Override
    public void create(Subscription s) throws Exception {
        String sql = "INSERT INTO subscription(id, service_name, price, start_date, end_date, status, type, months_engaged) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getId());
            ps.setString(2, s.getServiceName());
            ps.setDouble(3, s.getPrice());
            ps.setTimestamp(4, Timestamp.valueOf(s.getStartDate()));
            if (s.getEndDate() != null) ps.setTimestamp(5, Timestamp.valueOf(s.getEndDate())); else ps.setNull(5, Types.TIMESTAMP);
            ps.setString(6, s.getStatus().name());
            ps.setString(7, s.getClass().getSimpleName());
            if (s instanceof FixedSubscription) {
                ps.setInt(8, ((FixedSubscription) s).getMonthsEngaged());
            } else {
                ps.setNull(8, Types.INTEGER);
            }
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<Subscription> findById(String id) throws Exception {
        String sql = "SELECT * FROM subscription WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Subscription> findAll() throws Exception {
        String sql = "SELECT * FROM subscription ORDER BY service_name";
        List<Subscription> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public void update(Subscription s) throws Exception {
        String sql = "UPDATE subscription SET service_name=?, price=?, start_date=?, end_date=?, status=?, type=?, months_engaged=? WHERE id=?";
        try(Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);){

            ps.setString(1, s.getServiceName());
            ps.setDouble(2, s.getPrice());
            ps.setTimestamp(3, Timestamp.valueOf(s.getEndDate()));
            
            if (s.getEndDate() != null) ps.setTimestamp(4, Timestamp.valueOf(s.getEndDate())); else ps.setNull(4, Types.TIMESTAMP); 



        }
    }

    @Override
    public void delete(String id) throws Exception {
        String sql = "DELETE FROM subscription WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Subscription> findActive() throws Exception {
        String sql = "SELECT * FROM subscription WHERE status = ?";
        List<Subscription> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, Sstatus.ACTIVE.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    private Subscription mapRow(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String serviceName = rs.getString("service_name");
        double price = rs.getDouble("price");
        Timestamp tsStart = rs.getTimestamp("start_date");
        Timestamp tsEnd = rs.getTimestamp("end_date");
        String statusStr = rs.getString("status");
        String type = rs.getString("type");
        int monthsEngaged = rs.getInt("months_engaged");
        LocalDateTime start = tsStart.toLocalDateTime();
        LocalDateTime end = tsEnd == null ? null : tsEnd.toLocalDateTime();
        Sstatus status = Sstatus.valueOf(statusStr);

        if ("FixedSubscription".equals(type)) {
            return new FixedSubscription(id, serviceName, price, start, end, status, monthsEngaged);
        } else {
            return new FlexibleSubscription(id, serviceName, price, start, end, status);
        }
    }
}
