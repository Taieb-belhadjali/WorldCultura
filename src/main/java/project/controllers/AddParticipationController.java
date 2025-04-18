package project.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import project.models.event;
import project.models.participation;
import project.service.serviceparticipation;

public class AddParticipationController {

    private int eventId;

    @FXML
    private TextField eventIdField;

    @FXML
    private TextField nomField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField telephoneField;

    @FXML
    private Button submitBtn;

    serviceparticipation service = new serviceparticipation();

    public void setEventId(int id) {
        this.eventId = id;
        if (eventIdField != null) {
            eventIdField.setText(String.valueOf(id));
        }
    }

    @FXML
    private void handleSubmit() {
        String nom = nomField.getText();
        String email = emailField.getText();
        int telephone;

        try {
            telephone = Integer.parseInt(telephoneField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Téléphone invalide", "Veuillez entrer un numéro correct.");
            return;
        }

        // Créer la participation et l’enregistrer
        participation p = new participation(telephone, email, nom, eventId);
        service.add(p);

        showAlert(Alert.AlertType.INFORMATION, "Participation ajoutée", "Merci pour votre participation !");

        // Réinitialiser les champs
        nomField.clear();
        emailField.clear();
        telephoneField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void setSelectedEvent(event e) {
        this.eventId = e.getID();
        if (eventIdField != null) {
            eventIdField.setText(String.valueOf(eventId));
        }
    }

}
