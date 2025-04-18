package project.service;

import project.interfaces.iservice;
import project.models.event;
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
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, e.getName());
            pst.setString(2, e.getDate_debut());
            pst.setString(3, e.getDate_fin());
            pst.setString(4, e.getDescription());
            pst.setString(5, e.getImage());
            pst.executeUpdate();
            System.out.println("✅ Event added successfully.");
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
            pst.executeUpdate();
            System.out.println("✅ Event updated successfully.");
        } catch (SQLException ex) {
            System.err.println("❌ Error updating event: " + ex.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM event WHERE ID = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("✅ Event deleted successfully.");
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
        String sql = "SELECT * FROM event";
        try (Statement st = connection.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                event e = new event(
                        rs.getInt("ID"),
                        rs.getString("Name"),
                        rs.getString("date_debut"),
                        rs.getString("date_fin"),
                        rs.getString("Description"),
                        rs.getString("image")
                );
                events.add(e);
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error fetching events: " + ex.getMessage());
        }
        return events;
    }
}
