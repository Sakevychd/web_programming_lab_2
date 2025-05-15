package com.example.dao;

import com.example.model.Plane;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlaneDao {


   private Connection getConnection() throws SQLException {
    String url = "jdbc:postgresql://localhost:5432/airline_db";
    String username = "postgres";
    String password = "Dima#1212";

    return DriverManager.getConnection(url, username, password);
}


    public List<Plane> findAll() {
        List<Plane> planes = new ArrayList<>();
        String sql = "SELECT * FROM planes";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                planes.add(new Plane(
                        rs.getInt("id"),
                        rs.getString("model"),
                        rs.getInt("capacity"),
                        rs.getInt("manufacturer_id"),
                        rs.getInt("airline_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return planes;
    }


    public void save(Plane plane) {
    if (plane.getId() == 0) {
        insert(plane);
    } else {
        update(plane);
    }
}

    public Plane findById(int id) {
        String sql = "SELECT * FROM planes WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Plane(
                        rs.getInt("id"),
                        rs.getString("model"),
                        rs.getInt("capacity"),
                        rs.getInt("manufacturer_id"),
                        rs.getInt("airline_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insert(Plane plane) {
        String sql = "INSERT INTO planes (model, capacity, manufacturer_id, airline_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plane.getModel());
            stmt.setInt(2, plane.getCapacity());
            stmt.setInt(3, plane.getManufacturerId());
            stmt.setInt(4, plane.getAirlineId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Plane plane) {
        String sql = "UPDATE planes SET model = ?, capacity = ?, manufacturer_id = ?, airline_id = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plane.getModel());
            stmt.setInt(2, plane.getCapacity());
            stmt.setInt(3, plane.getManufacturerId());
            stmt.setInt(4, plane.getAirlineId());
            stmt.setInt(5, plane.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM planes WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
