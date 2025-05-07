package project.service.amineservice;

import project.interfaces.iservice;
import project.models.aminemodels.compagnie_aerienne;
import project.models.aminemodels.rehla;
import project.utils.Myconnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class rehlaservice implements iservice<rehla> {
    private Connection connection;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public rehlaservice() {
        connection = Myconnection.getInstance();
    }

    @Override
    public void add(rehla rehla) {
        String sql = "INSERT INTO rehla (agence_id, depart, destination, depart_date, arrival_date, price) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, rehla.getAgence().getId());
            pst.setString(2, rehla.getDepart());
            pst.setString(3, rehla.getDestination());
            pst.setString(4, rehla.getDepart_date().format(formatter));
            pst.setString(5, rehla.getArrival_date().format(formatter));
            pst.setFloat(6, rehla.getPrice());
            pst.executeUpdate();
            ResultSet generatedKeys = pst.getGeneratedKeys();
            if (generatedKeys.next()) {
                rehla.setId(generatedKeys.getInt(1));
            }
            System.out.println("✅ Flight (Rehla) added successfully with ID: " + rehla.getId());
        } catch (SQLException ex) {
            System.err.println("❌ Error adding flight (Rehla): " + ex.getMessage());
        }
    }

    @Override
    public void update(rehla rehla) {
        String sql = "UPDATE rehla SET agence_id = ?, depart = ?, destination = ?, depart_date = ?, arrival_date = ?, price = ? WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, rehla.getAgence().getId());
            pst.setString(2, rehla.getDepart());
            pst.setString(3, rehla.getDestination());
            pst.setString(4, rehla.getDepart_date().format(formatter));
            pst.setString(5, rehla.getArrival_date().format(formatter));
            pst.setFloat(6, rehla.getPrice());
            pst.setInt(7, rehla.getId());
            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Flight (Rehla) with ID " + rehla.getId() + " updated successfully.");
            } else {
                System.out.println("⚠️ Flight (Rehla) with ID " + rehla.getId() + " not found for update.");
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error updating flight (Rehla): " + ex.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM rehla WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Flight (Rehla) with ID " + id + " deleted successfully.");
            } else {
                System.out.println("⚠️ Flight (Rehla) with ID " + id + " not found for deletion.");
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error deleting flight (Rehla): " + ex.getMessage());
        }
    }

    @Override
    public rehla getById(int id) {
        String sql = "SELECT r.*, a.id as agence_id, a.nom as agence_nom, a.logo as agence_logo, a.description as agence_description, a.contact_du_responsable as agence_contact " +
                "FROM rehla r JOIN compagnie_aerienne a ON r.agence_id = a.id WHERE r.id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                rehla rehla = new rehla();
                rehla.setId(rs.getInt("id"));
                compagnie_aerienne agence = new compagnie_aerienne();
                agence.setId(rs.getInt("agence_id"));
                agence.setNom(rs.getString("agence_nom"));
                agence.setLogo(rs.getString("agence_logo"));
                agence.setDescription(rs.getString("agence_description"));
                agence.setContact_du_responsable(rs.getString("agence_contact"));
                rehla.setAgence(agence);
                rehla.setDepart(rs.getString("depart"));
                rehla.setDestination(rs.getString("destination"));
                rehla.setDepart_date(LocalDateTime.parse(rs.getString("depart_date"), formatter));
                rehla.setArrival_date(LocalDateTime.parse(rs.getString("arrival_date"), formatter));
                rehla.setPrice(rs.getFloat("price"));
                return rehla;
            } else {
                System.out.println("⚠️ Flight (Rehla) with ID " + id + " not found.");
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error fetching flight (Rehla): " + ex.getMessage());
        }
        return null;
    }

    @Override
    public List<rehla> getAll() {
        List<rehla> rehlas = new ArrayList<>();
        String sql = "SELECT r.*, a.id as agence_id, a.nom as agence_nom " +
                "FROM rehla r JOIN compagnie_aerienne a ON r.agence_id = a.id";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                rehla rehla = new rehla();
                rehla.setId(rs.getInt("id"));
                compagnie_aerienne agence = new compagnie_aerienne();
                agence.setId(rs.getInt("agence_id"));
                agence.setNom(rs.getString("agence_nom"));
                rehla.setAgence(agence);
                rehla.setDepart(rs.getString("depart"));
                rehla.setDestination(rs.getString("destination"));
                rehla.setDepart_date(LocalDateTime.parse(rs.getString("depart_date"), formatter));
                rehla.setArrival_date(LocalDateTime.parse(rs.getString("arrival_date"), formatter));
                rehla.setPrice(rs.getFloat("price"));
                rehlas.add(rehla);
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error fetching all flights (Rehlas): " + ex.getMessage());
        }
        return rehlas;
    }
}