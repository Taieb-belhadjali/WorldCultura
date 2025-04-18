package project.controllers;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import project.models.participation;
import project.service.serviceparticipation;

public class UpdateParticipationController {

    @FXML
    private TextField tfNom;
    @FXML
    private TextField tfEmail;
    @FXML
    private TextField tfTelephone;
    @FXML
    private TextField tfEventId;
    @FXML
    private Button btnUpdate;

    private serviceparticipation service = new serviceparticipation();

    private participation participationToUpdate;

    // Méthode pour initialiser la vue avec les données de la participation
    public void initData(participation participation) {
        this.participationToUpdate = participation;
        tfNom.setText(participation.getNom());
        tfEmail.setText(participation.getEmail());
        tfTelephone.setText(String.valueOf(participation.getTelephone()));
        tfEventId.setText(String.valueOf(participation.getEvent_id()));
    }

    // Méthode de mise à jour de la participation
    @FXML
    private void handleUpdate() {
        try {
            String nom = tfNom.getText();
            String email = tfEmail.getText();
            int telephone = Integer.parseInt(tfTelephone.getText());
            int eventId = Integer.parseInt(tfEventId.getText());

            // Mettre à jour la participation
            participation updatedParticipation = new participation(participationToUpdate.getId(), eventId, nom, email, telephone);
            service.update(updatedParticipation);

            // Affichage d'un message de succès
            showAlert("Mise à jour réussie", "La participation a été mise à jour avec succès.", Alert.AlertType.INFORMATION);
        } catch (NumberFormatException e) {
            // Affichage d'une alerte si les champs ne sont pas valides
            showAlert("Erreur", "Veuillez vérifier les informations saisies.", Alert.AlertType.ERROR);
        }
    }

    // Méthode pour afficher une alerte
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
