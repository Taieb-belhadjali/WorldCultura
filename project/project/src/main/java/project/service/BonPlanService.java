package project.service;

import project.models.BonPlan;
import project.utils.Myconnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BonPlanService {

    // Removed database credentials and URL - using Myconnection
    // private static final String DB_URL = "jdbc:mysql://localhost:3306/your_database_name";
    // private static final String DB_USER = "your_username";
    // private static final String DB_PASSWORD = "your_password";

    public List<BonPlan> getAll() {
        List<BonPlan> bonPlans = new ArrayList<>();
        Connection conn = null; // Declare connection outside the try block
        try {
            conn = Myconnection.getInstance(); // Get connection from your class
            if (conn == null) {
                System.err.println("Failed to get database connection.");
                return null; // Or throw an exception
            }
            PreparedStatement pstmt = conn.prepareStatement("SELECT id, titre, description, lieu, date_creation, date_expiration, categorie FROM bon_plan"); // Replace "bon_plan" with your actual table name
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                BonPlan bonPlan = new BonPlan();
                bonPlan.setId(rs.getInt("id"));             // IMPORTANT:  Fetch the ID from the result set!
                bonPlan.setTitre(rs.getString("titre"));
                bonPlan.setDescription(rs.getString("description"));
                bonPlan.setLieu(rs.getString("lieu"));
                try {
                    bonPlan.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime()); // Correct type conversion
                    bonPlan.setDateExpiration(rs.getDate("date_expiration").toLocalDate());       // Correct type conversion
                } catch (SQLException e) {
                    System.err.println("Error converting date/time: " + e.getMessage());
                    bonPlan.setDateCreation(null);  // Or set a default:  bonPlan.setDateCreation(LocalDateTime.now());
                    bonPlan.setDateExpiration(null); //Or set a default value
                }
                bonPlan.setCategorie(rs.getString("categorie"));
                bonPlans.add(bonPlan);
            }
        } catch (SQLException e) {
            e.printStackTrace(); //  Handle this exception properly (log it, or rethrow it)
            return null; // Or throw an exception
        } finally {
            // Removed closing of connection here.  Your Myconnection class is likely handling this, or you might have a separate utility method.
            // try { if (conn != null) conn.close(); } catch (SQLException ex) {}
        }
        return bonPlans;
    }

    public BonPlan getById(int id) {
        BonPlan bonPlan = null;
        Connection conn = null;
        try {
            conn = Myconnection.getInstance();
            if (conn == null) {
                System.err.println("Failed to get database connection.");
                return null; // Or throw an exception
            }
            PreparedStatement pstmt = conn.prepareStatement("SELECT id, titre, description, lieu, date_creation, date_expiration, categorie FROM bon_plan WHERE id = ?"); // Replace "bon_plan" with your actual table name
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                bonPlan = new BonPlan();
                bonPlan.setId(rs.getInt("id"));
                bonPlan.setTitre(rs.getString("titre"));
                bonPlan.setDescription(rs.getString("description"));
                bonPlan.setLieu(rs.getString("lieu"));
                try {
                    bonPlan.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
                    bonPlan.setDateExpiration(rs.getDate("date_expiration").toLocalDate());
                } catch (SQLException e) {
                    System.err.println("Error converting date/time: " + e.getMessage());
                    bonPlan.setDateCreation(null);
                    bonPlan.setDateExpiration(null);
                }
                bonPlan.setCategorie(rs.getString("categorie"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            // Removed closing of connection here.
            // try { if (conn != null) conn.close(); } catch (SQLException ex) {}
        }
        return bonPlan;
    }
    public void add(BonPlan bonPlan) {
        Connection conn = null;
        try {
            conn = Myconnection.getInstance();
            if (conn == null) {
                System.err.println("Failed to get database connection.");
                return; // Or throw an exception
            }
            String sql = "INSERT INTO bon_plan (titre, description, lieu, date_creation, date_expiration, categorie) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, bonPlan.getTitre());
            pstmt.setString(2, bonPlan.getDescription());
            pstmt.setString(3, bonPlan.getLieu());
            pstmt.setTimestamp(4, java.sql.Timestamp.valueOf(bonPlan.getDateCreation())); // Convert LocalDateTime
            pstmt.setDate(5, java.sql.Date.valueOf(bonPlan.getDateExpiration()));       // Convert LocalDate
            pstmt.setString(6, bonPlan.getCategorie());
            pstmt.executeUpdate();
            System.out.println("Bon Plan added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception: log, throw, inform the user
        } finally {
            // Removed connection closing
        }
    }

    public void update(BonPlan bonPlan) {
        Connection conn = null;
        try {
            conn = Myconnection.getInstance();
            if (conn == null) {
                System.err.println("Failed to get database connection.");
                return; // Or throw an exception
            }
            String sql = "UPDATE bon_plan SET titre = ?, description = ?, lieu = ?, date_creation = ?, date_expiration = ?, categorie = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, bonPlan.getTitre());
            pstmt.setString(2, bonPlan.getDescription());
            pstmt.setString(3, bonPlan.getLieu());
            pstmt.setTimestamp(4, java.sql.Timestamp.valueOf(bonPlan.getDateCreation()));
            pstmt.setDate(5, java.sql.Date.valueOf(bonPlan.getDateExpiration()));
            pstmt.setString(6, bonPlan.getCategorie());
            pstmt.setInt(7, bonPlan.getId());
            pstmt.executeUpdate();
            System.out.println("Bon Plan updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception
        } finally {
            // Removed connection closing
        }
    }

    public void delete(int id) {
        Connection conn = null;
        try {
            conn = Myconnection.getInstance();
            if (conn == null) {
                System.err.println("Failed to get database connection.");
                return; // Or throw an exception
            }
            String sql = "DELETE FROM bon_plan WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Bon Plan deleted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception
        } finally {
            // Removed connection closing
        }
    }
}

