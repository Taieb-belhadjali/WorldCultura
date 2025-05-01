package project.service;

import project.interfaces.iservice;
import project.models.Reservation;
import project.models.rehla;
import project.models.compagnie_aerienne; // Importez la classe de l'agence
import project.utils.Myconnection;

import java.sql.*;
import java.time.LocalDateTime;
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
            System.out.println("✅ Réservation ajoutée avec succès avec l'ID : " + reservation.getId() + " pour le vol ID : " + reservation.getRehla().getId());
        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de l'ajout de la réservation : " + ex.getMessage());
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
                System.out.println("✅ Réservation avec l'ID " + reservation.getId() + " mise à jour avec succès pour le vol ID : " + reservation.getRehla().getId());
            } else {
                System.out.println("⚠️ Réservation avec l'ID " + reservation.getId() + " non trouvée pour la mise à jour.");
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de la mise à jour de la réservation : " + ex.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM reservation WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Réservation avec l'ID " + id + " supprimée avec succès.");
            } else {
                System.out.println("⚠️ Réservation avec l'ID " + id + " non trouvée pour la suppression.");
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de la suppression de la réservation : " + ex.getMessage());
        }
    }

    @Override
    public Reservation getById(int id) {
        String sql = "SELECT r.*, h.*, a.* FROM reservation r JOIN rehla h ON r.rehla_id = h.id JOIN compagnie_aerienne a ON h.agence_id = a.id WHERE r.id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setId(rs.getInt("id"));

                rehla flight = new rehla();
                flight.setId(rs.getInt("rehla_id"));
                flight.setDepart(rs.getString("depart"));
                flight.setDestination(rs.getString("destination"));
                flight.setDepart_date(rs.getObject("depart_date", LocalDateTime.class));
                flight.setArrival_date(rs.getObject("arrival_date", LocalDateTime.class));
                flight.setPrice(rs.getFloat("price"));

                // Récupération de l'agence
                compagnie_aerienne agence = new compagnie_aerienne();
                agence.setId(rs.getInt("agence_id")); // Assurez-vous que le nom de la colonne est correct
                agence.setNom(rs.getString("nom"));    // Assurez-vous que le nom de la colonne est correct
                //agence.setAdresse(rs.getString("adresse")); // Si vous avez d'autres attributs pour l'agence
                flight.setAgence(agence);

                reservation.setRehla(flight);
                reservation.setUserName(rs.getString("user_name"));
                reservation.setEmail(rs.getString("email"));
                reservation.setContact(rs.getString("contact"));
                reservation.setUserId((Integer) rs.getObject("user_id"));
                return reservation;
            } else {
                System.out.println("⚠️ Réservation avec l'ID " + id + " non trouvée.");
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de la récupération de la réservation : " + ex.getMessage());
        }
        return null;
    }

    @Override
    public List<Reservation> getAll() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.*, h.*, a.* FROM reservation r JOIN rehla h ON r.rehla_id = h.id JOIN compagnie_aerienne a ON h.agence_id = a.id";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setId(rs.getInt("id"));

                rehla flight = new rehla();
                flight.setId(rs.getInt("rehla_id"));
                flight.setDepart(rs.getString("depart"));
                flight.setDestination(rs.getString("destination"));
                flight.setDepart_date(rs.getObject("depart_date", LocalDateTime.class));
                flight.setArrival_date(rs.getObject("arrival_date", LocalDateTime.class));
                flight.setPrice(rs.getFloat("price"));

                // Récupération de l'agence
                compagnie_aerienne agence = new compagnie_aerienne();
                agence.setId(rs.getInt("agence_id"));  // Assurez-vous que le nom de la colonne est correct
                agence.setNom(rs.getString("nom"));     // Assurez-vous que le nom de la colonne est correct
                //agence.setAdresse(rs.getString("adresse"));  // Si vous avez d'autres attributs pour l'agence
                flight.setAgence(agence);

                reservation.setRehla(flight);
                reservation.setUserName(rs.getString("user_name"));
                reservation.setEmail(rs.getString("email"));
                reservation.setContact(rs.getString("contact"));
                reservation.setUserId((Integer) rs.getObject("user_id"));
                reservations.add(reservation);
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de la récupération de toutes les réservations : " + ex.getMessage());
        }
        return reservations;
    }
}
