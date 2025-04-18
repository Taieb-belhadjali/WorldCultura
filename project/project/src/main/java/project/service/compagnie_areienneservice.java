package project.service;

import project.interfaces.iservice;
import project.models.compagnie_aerienne;
import project.utils.Myconnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class compagnie_areienneservice implements iservice<compagnie_aerienne> {
    private Connection connection;

    public compagnie_areienneservice() {
        connection = Myconnection.getInstance();
    }

    @Override
    public void add(compagnie_aerienne compagnieAerienne) {
        String sql = "INSERT INTO compagnie_aerienne (nom, logo, description, contact_du_responsable) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, compagnieAerienne.getNom());
            pst.setString(2, compagnieAerienne.getLogo());
            pst.setString(3, compagnieAerienne.getDescription());
            pst.setString(4, compagnieAerienne.getContact_du_responsable());
            pst.executeUpdate();
            ResultSet generatedKeys = pst.getGeneratedKeys();
            if (generatedKeys.next()) {
                compagnieAerienne.setId(generatedKeys.getInt(1));
            }
            System.out.println("✅ Airline added successfully with ID: " + compagnieAerienne.getId());
        } catch (SQLException ex) {
            System.err.println("❌ Error adding airline: " + ex.getMessage());
        }
    }

    @Override
    public void update(compagnie_aerienne compagnieAerienne) {
        String sql = "UPDATE compagnie_aerienne SET nom = ?, logo = ?, description = ?, contact_du_responsable = ? WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, compagnieAerienne.getNom());
            pst.setString(2, compagnieAerienne.getLogo());
            pst.setString(3, compagnieAerienne.getDescription());
            pst.setString(4, compagnieAerienne.getContact_du_responsable());
            pst.setInt(5, compagnieAerienne.getId());
            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Airline with ID " + compagnieAerienne.getId() + " updated successfully.");
            } else {
                System.out.println("⚠️ Airline with ID " + compagnieAerienne.getId() + " not found for update.");
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error updating airline: " + ex.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM compagnie_aerienne WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Airline with ID " + id + " deleted successfully.");
            } else {
                System.out.println("⚠️ Airline with ID " + id + " not found for deletion.");
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error deleting airline: " + ex.getMessage());
        }
    }

    @Override
    public compagnie_aerienne getById(int id) {
        String sql = "SELECT * FROM compagnie_aerienne WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                compagnie_aerienne compagnie = new compagnie_aerienne();
                compagnie.setId(rs.getInt("id"));
                compagnie.setNom(rs.getString("nom"));
                compagnie.setLogo(rs.getString("logo"));
                compagnie.setDescription(rs.getString("description"));
                compagnie.setContact_du_responsable(rs.getString("contact_du_responsable"));
                return compagnie;
            } else {
                System.out.println("⚠️ Airline with ID " + id + " not found.");
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error fetching airline: " + ex.getMessage());
        }
        return null;
    }

    @Override
    public List<compagnie_aerienne> getAll() {
        List<compagnie_aerienne> compagnies = new ArrayList<>();
        String sql = "SELECT * FROM compagnie_aerienne";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                compagnie_aerienne compagnie = new compagnie_aerienne();
                compagnie.setId(rs.getInt("id"));
                compagnie.setNom(rs.getString("nom"));
                compagnie.setLogo(rs.getString("logo"));
                compagnie.setDescription(rs.getString("description"));
                compagnie.setContact_du_responsable(rs.getString("contact_du_responsable"));
                compagnies.add(compagnie);
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error fetching all airlines: " + ex.getMessage());
        }
        return compagnies;
    }
}