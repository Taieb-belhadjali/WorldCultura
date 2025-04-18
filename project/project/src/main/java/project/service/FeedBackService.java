package project.service;

import project.interfaces.iservice;
import project.models.FeedBack;
import project.utils.Myconnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class FeedBackService implements iservice<FeedBack> {

    private Connection connection;

    public FeedBackService() {
        connection = Myconnection.getInstance();
    }

    @Override
    public void add(FeedBack feedback) {
        String sql = "INSERT INTO feedback (bon_plan_id, commentaire, rating, date_creation) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, feedback.getBonPlanId());
            pst.setString(2, feedback.getCommentaire());
            pst.setInt(3, feedback.getRating());
            pst.setTimestamp(4, feedback.getDateCreation() != null ? Timestamp.valueOf(feedback.getDateCreation()) : null); //ADDED

            pst.executeUpdate();

            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                feedback.setId(rs.getInt(1));
            }
            System.out.println("✅ Feedback ajouté avec ID: " + feedback.getId());
        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de l'ajout du feedback: " + ex.getMessage());
        }
    }

    @Override
    public void update(FeedBack feedback) {
        String sql = "UPDATE feedback SET bon_plan_id = ?, commentaire = ?, rating = ?, date_creation = ? WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, feedback.getBonPlanId());
            pst.setString(2, feedback.getCommentaire());
            pst.setInt(3, feedback.getRating());
            pst.setTimestamp(4, feedback.getDateCreation() != null ? Timestamp.valueOf(feedback.getDateCreation()) : null); //ADDED
            pst.setInt(5, feedback.getId());

            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Feedback ID " + feedback.getId() + " mis à jour.");
            } else {
                System.out.println("⚠️ Feedback ID " + feedback.getId() + " introuvable.");
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur mise à jour feedback: " + ex.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM feedback WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Feedback ID " + id + " supprimé.");
            } else {
                System.out.println("⚠️ Feedback ID " + id + " introuvable.");
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur suppression feedback: " + ex.getMessage());
        }
    }

    @Override
    public FeedBack getById(int id) {
        String sql = "SELECT id, bon_plan_id, commentaire, rating, date_creation FROM feedback WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                FeedBack fb = new FeedBack();
                fb.setId(rs.getInt("id"));
                fb.setBonPlanId(rs.getInt("bon_plan_id"));
                fb.setCommentaire(rs.getString("commentaire"));
                fb.setRating(rs.getInt("rating"));
                //handle date
                fb.setDateCreation(rs.getTimestamp("date_creation") != null ? rs.getTimestamp("date_creation").toLocalDateTime() : null);
                return fb;
            } else {
                System.out.println("⚠️ Feedback ID " + id + " introuvable.");
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur récupération feedback: " + ex.getMessage());
        }
        return null;
    }

    @Override
    public List<FeedBack> getAll() {
        List<FeedBack> feedbacks = new ArrayList<>();
        String sql = "SELECT id, bon_plan_id, commentaire, rating, date_creation FROM feedback";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                FeedBack fb = new FeedBack();
                fb.setId(rs.getInt("id"));
                fb.setBonPlanId(rs.getInt("bon_plan_id"));
                fb.setCommentaire(rs.getString("commentaire"));
                fb.setRating(rs.getInt("rating"));
                //handle date
                fb.setDateCreation(rs.getTimestamp("date_creation") != null ? rs.getTimestamp("date_creation").toLocalDateTime() : null);
                feedbacks.add(fb);
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur récupération feedbacks: " + ex.getMessage());
        }
        return feedbacks;
    }

    // Extra method (optional): Récupérer les feedbacks pour un bon plan spécifique
    public List<FeedBack> getFeedbacksByBonPlanId(int bonPlanId) {
        List<FeedBack> feedbacks = new ArrayList<>();
        String sql = "SELECT id, bon_plan_id, commentaire, rating, date_creation FROM feedback WHERE bon_plan_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, bonPlanId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                FeedBack fb = new FeedBack();
                fb.setId(rs.getInt("id"));
                fb.setBonPlanId(rs.getInt("bon_plan_id"));
                fb.setCommentaire(rs.getString("commentaire"));
                fb.setRating(rs.getInt("rating"));
                //handle date
                fb.setDateCreation(rs.getTimestamp("date_creation") != null ? rs.getTimestamp("date_creation").toLocalDateTime() : null);
                feedbacks.add(fb);
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur récupération feedbacks par bon plan: " + ex.getMessage());
        }
        return feedbacks;
    }
}
