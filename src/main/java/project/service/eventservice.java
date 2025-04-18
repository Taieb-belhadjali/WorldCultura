package project.service;

import project.interfaces.iservice;
import project.models.event;
import project.models.participation;
import project.utils.Myconnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class eventservice implements iservice<event> {
    private Connection connection;

    public eventservice() {
        connection = Myconnection.getInstance();
    }

    @Override
    public void add(event e) {
        String sql = "INSERT INTO event (Name, date_debut, date_fin, Description, image) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, e.getName());
            pst.setString(2, e.getDate_debut());
            pst.setString(3, e.getDate_fin());
            pst.setString(4, e.getDescription());
            pst.setString(5, e.getImage());

            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        e.setID(rs.getInt(1));
                    }
                }
                System.out.println("✅ Event added successfully. ID: " + e.getID());
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error adding event: " + ex.getMessage());
        }
    }

    @Override
    public void update(event e) {
        String sql = "UPDATE event SET Name = ?, date_debut = ?, date_fin = ?, Description = ?, image = ? WHERE ID = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, e.getName());
            pst.setString(2, e.getDate_debut());
            pst.setString(3, e.getDate_fin());
            pst.setString(4, e.getDescription());
            pst.setString(5, e.getImage());
            pst.setInt(6, e.getID());

            int rowsUpdated = pst.executeUpdate();
            System.out.println(rowsUpdated > 0 ? "✅ Event updated successfully." : "⚠ No event found with ID: " + e.getID());
        } catch (SQLException ex) {
            System.err.println("❌ Error updating event: " + ex.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        // D'abord supprimer les participations liées
        new serviceparticipation().deleteByEventId(id);

        // Puis supprimer l'événement
        String sql = "DELETE FROM event WHERE ID = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            int rowsDeleted = pst.executeUpdate();
            System.out.println(rowsDeleted > 0 ? "✅ Event and related participations deleted." : "⚠ No event found with ID: " + id);
        } catch (SQLException ex) {
            System.err.println("❌ Error deleting event: " + ex.getMessage());
        }
    }

    @Override
    public event getById(int id) {
        String sql = "SELECT * FROM event WHERE ID = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new event(
                        rs.getInt("ID"),
                        rs.getString("Name"),
                        rs.getString("date_debut"),
                        rs.getString("date_fin"),
                        rs.getString("Description"),
                        rs.getString("image")
                );
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error fetching event: " + ex.getMessage());
        }
        return null;
    }

    @Override
    public List<event> getAll() {
        List<event> events = new ArrayList<>();
        String sql = "SELECT * FROM event ORDER BY date_debut DESC";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                events.add(new event(
                        rs.getInt("ID"),
                        rs.getString("Name"),
                        rs.getString("date_debut"),
                        rs.getString("date_fin"),
                        rs.getString("Description"),
                        rs.getString("image")
                ));
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error fetching events: " + ex.getMessage());
        }
        return events;
    }

    // Méthodes spécifiques pour les jointures
    public event getEventWithParticipations(int eventId) {
        event ev = this.getById(eventId);
        if (ev != null) {
            List<participation> participations = new serviceparticipation().getByEventId(eventId);
            ev.setParticipations(participations);
        }
        return ev;
    }

    public List<event> getAllEventsWithParticipations() {
        List<event> events = this.getAll();
        serviceparticipation sp = new serviceparticipation();

        for (event e : events) {
            e.setParticipations(sp.getByEventId(e.getID()));
        }

        return events;
    }

    public int getParticipationCountForEvent(int eventId) {
        String sql = "SELECT COUNT(*) FROM participation WHERE event_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, eventId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error counting participations: " + ex.getMessage());
        }
        return 0;
    }
}