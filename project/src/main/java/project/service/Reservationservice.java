package project.service;

import project.interfaces.iservice;
import project.models.Reservation;
import project.models.rehla;
import project.utils.Myconnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Reservationservice implements iservice<Reservation> {
    private Connection connection;

    public Reservationservice() {
        connection = Myconnection.getInstance();
    }

    @Override
    public void add(Reservation reservation) {
        String sql = "INSERT INTO reservation (rehla_id, user_name, email, contact, user_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, reservation.getRehla().getId());
            pst.setString(2, reservation.getUserName());
            pst.setString(3, reservation.getEmail());
            pst.setString(4, reservation.getContact());
            pst.setObject(5, reservation.getUserId()); // Allows null for userId
            pst.executeUpdate();
            ResultSet generatedKeys = pst.getGeneratedKeys();
            if (generatedKeys.next()) {
                reservation.setId(generatedKeys.getInt(1));
            }
            System.out.println("✅ Reservation added successfully with ID: " + reservation.getId() + " for Flight ID: " + reservation.getRehla().getId());
        } catch (SQLException ex) {
            System.err.println("❌ Error adding reservation: " + ex.getMessage());
        }
    }

    @Override
    public void update(Reservation reservation) {
        String sql = "UPDATE reservation SET rehla_id = ?, user_name = ?, email = ?, contact = ?, user_id = ? WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, reservation.getRehla().getId());
            pst.setString(2, reservation.getUserName());
            pst.setString(3, reservation.getEmail());
            pst.setString(4, reservation.getContact());
            pst.setObject(5, reservation.getUserId()); // Allows null for userId
            pst.setInt(6, reservation.getId());
            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Reservation with ID " + reservation.getId() + " updated successfully for Flight ID: " + reservation.getRehla().getId());
            } else {
                System.out.println("⚠️ Reservation with ID " + reservation.getId() + " not found for update.");
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error updating reservation: " + ex.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM reservation WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Reservation with ID " + id + " deleted successfully.");
            } else {
                System.out.println("⚠️ Reservation with ID " + id + " not found for deletion.");
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error deleting reservation: " + ex.getMessage());
        }
    }

    @Override
    public Reservation getById(int id) {
        String sql = "SELECT r.*, h.id as rehla_id FROM reservation r JOIN rehla h ON r.rehla_id = h.id WHERE r.id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setId(rs.getInt("id"));
                rehla flight = new rehla();
                flight.setId(rs.getInt("rehla_id"));
                reservation.setRehla(flight);
                reservation.setUserName(rs.getString("user_name")); // Changed to user_name
                reservation.setEmail(rs.getString("email"));
                reservation.setContact(rs.getString("contact"));
                reservation.setUserId((Integer) rs.getObject("user_id")); // Changed to user_id
                return reservation;
            } else {
                System.out.println("⚠️ Reservation with ID " + id + " not found.");
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error fetching reservation: " + ex.getMessage());
        }
        return null;
    }

    @Override
    public List<Reservation> getAll() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.*, h.id as rehla_id FROM reservation r JOIN rehla h ON r.rehla_id = h.id";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setId(rs.getInt("id"));
                rehla flight = new rehla();
                flight.setId(rs.getInt("rehla_id"));
                reservation.setRehla(flight);
                reservation.setUserName(rs.getString("user_name")); // Changed to user_name
                reservation.setEmail(rs.getString("email"));
                reservation.setContact(rs.getString("contact"));
                reservation.setUserId((Integer) rs.getObject("user_id")); // Changed to user_id
                reservations.add(reservation);
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error fetching all reservations: " + ex.getMessage());
        }
        return reservations;
    }
}