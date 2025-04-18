package project.service;

import project.interfaces.iservice;
import project.models.participation;
import project.utils.Myconnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class serviceparticipation implements iservice<participation> {
    private static final Connection connection = Myconnection.getInstance();

    @Override
    public void add(participation p) {
        String query = "INSERT INTO participation (event_id, nom, email, telephone) VALUES (?, ?, ?, ?)";
        try {
            connection.setAutoCommit(false); // Start transaction

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, p.getEvent_id());
                stmt.setString(2, p.getNom());
                stmt.setString(3, p.getEmail());
                stmt.setInt(4, p.getTelephone());

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("✅ Participation added successfully.");
                } else {
                    System.out.println("❌ Failed to add participation.");
                }

                connection.commit(); // Commit transaction
            } catch (SQLException e) {
                connection.rollback(); // Rollback transaction on failure
                System.err.println("❌ Database error: " + e.getMessage());
            } finally {
                connection.setAutoCommit(true); // Restore auto-commit mode
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to start transaction: " + e.getMessage());
        }
    }

    @Override
    public void update(participation p) {
        String query = "UPDATE participation SET event_id = ?, nom = ?, email = ?, telephone = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, p.getEvent_id());
            stmt.setString(2, p.getNom());
            stmt.setString(3, p.getEmail());
            stmt.setInt(4, p.getTelephone());
            stmt.setInt(5, p.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Participation updated successfully.");
            } else {
                System.out.println("❌ Failed to update participation.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Database error: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM participation WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Participation deleted successfully.");
            } else {
                System.out.println("❌ Failed to delete participation.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Database error: " + e.getMessage());
        }
    }

    @Override
    public participation getById(int id) {
        String query = "SELECT * FROM participation WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int eventId = rs.getInt("event_id");
                String nom = rs.getString("nom");
                String email = rs.getString("email");
                int telephone = rs.getInt("telephone");

                return new participation(id, eventId, nom, email, telephone);
            }
        } catch (SQLException e) {
            System.err.println("❌ Database error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<participation> getAll() {
        List<participation> participations = new ArrayList<>();
        String query = "SELECT * FROM participation";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                int eventId = rs.getInt("event_id");
                String nom = rs.getString("nom");
                String email = rs.getString("email");
                int telephone = rs.getInt("telephone");

                participations.add(new participation(id, eventId, nom, email, telephone));
            }
        } catch (SQLException e) {
            System.err.println("❌ Database error: " + e.getMessage());
        }
        return participations;
    }
}
