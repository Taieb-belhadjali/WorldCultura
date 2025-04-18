package project.service;

import project.interfaces.iservice;
import project.models.event;
import project.models.participation;
import project.utils.Myconnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class serviceparticipation implements iservice<participation> {
    private Connection connection = Myconnection.getInstance();

    @Override
    public void add(participation p) {
        String query = "INSERT INTO participation (event_id, nom, email, telephone) VALUES (?, ?, ?, ?)";
        try {
            connection.setAutoCommit(false);

            try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, p.getEvent_id());
                stmt.setString(2, p.getNom());
                stmt.setString(3, p.getEmail());
                stmt.setInt(4, p.getTelephone());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            p.setId(rs.getInt(1));
                        }
                    }
                    System.out.println("✅ Participation added successfully. ID: " + p.getId());
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("❌ Database error: " + e.getMessage());
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
            System.out.println(rowsAffected > 0 ? "✅ Participation updated." : "⚠ No participation found with ID: " + p.getId());
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
            System.out.println(rowsAffected > 0 ? "✅ Participation deleted." : "⚠ No participation found with ID: " + id);
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
                return createParticipationFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Database error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<participation> getAll() {
        List<participation> participations = new ArrayList<>();
        String query = "SELECT * FROM participation ORDER BY nom";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                participations.add(createParticipationFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Database error: " + e.getMessage());
        }
        return participations;
    }

    public List<participation> getByEventId(int eventId) {
        List<participation> participations = new ArrayList<>();
        String query = "SELECT * FROM participation WHERE event_id = ? ORDER BY nom";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                participations.add(createParticipationFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Database error: " + e.getMessage());
        }
        return participations;
    }

    public void deleteByEventId(int eventId) {
        String sql = "DELETE FROM participation WHERE event_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, eventId);
            int count = pst.executeUpdate();
            System.out.println("✅ " + count + " participations deleted for event ID: " + eventId);
        } catch (SQLException ex) {
            System.err.println("❌ Error deleting participations: " + ex.getMessage());
        }
    }

    public participation getParticipationWithEvent(int participationId) {
        participation p = this.getById(participationId);
        if (p != null) {
            p.setEventObj(new eventservice().getById(p.getEvent_id()));
        }
        return p;
    }

    public List<participation> getAllParticipationsWithEvents() {
        List<participation> participations = this.getAll();
        eventservice es = new eventservice();

        for (participation p : participations) {
            p.setEventObj(es.getById(p.getEvent_id()));
        }

        return participations;
    }

    private participation createParticipationFromResultSet(ResultSet rs) throws SQLException {
        participation p = new participation(
                rs.getInt("id"),
                rs.getInt("event_id"),
                rs.getString("nom"),
                rs.getString("email"),
                rs.getInt("telephone")
        );

        // Optionnel: Charger l'événement associé
        // p.setEventObj(new eventservice().getById(p.getEvent_id()));

        return p;
    }
}