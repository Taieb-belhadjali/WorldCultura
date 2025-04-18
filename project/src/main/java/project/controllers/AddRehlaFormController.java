// project/controllers/AddRehlaFormController.java
package project.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import project.models.compagnie_aerienne;
import project.models.rehla;
import project.service.compagnie_areienneservice;
import project.service.rehlaservice;

import java.time.LocalDate; // Added import
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AddRehlaFormController {

    @FXML
    private ComboBox<compagnie_aerienne> agenceComboBox;

    @FXML
    private TextField departTextField;

    @FXML
    private TextField destinationTextField;

    @FXML
    private DatePicker departDatePicker;

    @FXML
    private TextField departTimeTextField;

    @FXML
    private DatePicker arrivalDatePicker;

    @FXML
    private TextField arrivalTimeTextField;

    @FXML
    private TextField priceTextField;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private final rehlaservice rehlaService = new rehlaservice();
    private final compagnie_areienneservice compagnieAerienneService = new compagnie_areienneservice();
    private ListRehlaController listRehlaController;

    @FXML
    public void initialize() {
        loadAgences();
    }

    public void setListRehlaController(ListRehlaController controller) {
        this.listRehlaController = controller;
    }

    private void loadAgences() {
        List<compagnie_aerienne> agences = compagnieAerienneService.getAll();
        agenceComboBox.setItems(javafx.collections.FXCollections.observableArrayList(agences));
    }

    @FXML
    private void handleSave() {
        compagnie_aerienne selectedAgence = agenceComboBox.getValue();
        String depart = departTextField.getText();
        String destination = destinationTextField.getText();
        LocalDate departDate = departDatePicker.getValue(); // LocalDate is now resolved
        String departTimeStr = departTimeTextField.getText();
        LocalDate arrivalDate = arrivalDatePicker.getValue(); // LocalDate is now resolved
        String arrivalTimeStr = arrivalTimeTextField.getText();
        String priceStr = priceTextField.getText();

        if (selectedAgence == null || depart.isEmpty() || destination.isEmpty() || departDate == null || departTimeStr.isEmpty() || arrivalDate == null || arrivalTimeStr.isEmpty() || priceStr.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Veuillez remplir tous les champs.");
            return;
        }

        try {
            LocalTime departTime = LocalTime.parse(departTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
            LocalDateTime departDateTime = LocalDateTime.of(departDate, departTime); // Should now work correctly

            LocalTime arrivalTime = LocalTime.parse(arrivalTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
            LocalDateTime arrivalDateTime = LocalDateTime.of(arrivalDate, arrivalTime); // Should now work correctly

            float price = Float.parseFloat(priceStr);

            rehla newRehla = new rehla();
            newRehla.setAgence(selectedAgence);
            newRehla.setDepart(depart);
            newRehla.setDestination(destination);
            newRehla.setDepart_date(departDateTime);
            newRehla.setArrival_date(arrivalDateTime);
            newRehla.setPrice(price);

            rehlaService.add(newRehla); // Utilisation du service pour l'ajout

            if (listRehlaController != null) {
                listRehlaController.loadRehlas();
            }

            // Fermer la fenêtre d'ajout
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();

        } catch (java.time.format.DateTimeParseException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de format", "Veuillez entrer une heure valide au format HH:mm.");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de format", "Veuillez entrer un prix valide.");
        }
    }

    @FXML
    private void handleCancel() {
        // Fermer la fenêtre d'ajout
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}