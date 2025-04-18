package project.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class updaterehlacontroller {

    @FXML
    private ComboBox<compagnie_aerienne> agenceComboBox;
    @FXML
    private TextField departTextField;
    @FXML
    private TextField destinationTextField;
    @FXML
    private DatePicker departDatePicker;
    @FXML
    private ComboBox<LocalTime> departTimeComboBox;
    @FXML
    private DatePicker arrivalDatePicker;
    @FXML
    private ComboBox<LocalTime> arrivalTimeComboBox;
    @FXML
    private TextField priceTextField;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button confirmButton;

    private rehla rehlaToEdit;
    private final rehlaservice rehlaService = new rehlaservice();
    private final compagnie_areienneservice agenceService = new compagnie_areienneservice();
    private ListRehlaController listRehlaController;
    private Stage dialogStage; // To manage the stage of this dialog
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @FXML
    public void initialize() {
        List<compagnie_aerienne> agences = agenceService.getAll();
        agenceComboBox.setItems(FXCollections.observableArrayList(agences));

        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 60; j += 30) {
                departTimeComboBox.getItems().add(LocalTime.of(i, j));
                arrivalTimeComboBox.getItems().add(LocalTime.of(i, j));
            }
        }
    }

    public void setRehlaToEdit(rehla rehla) {
        this.rehlaToEdit = rehla;
        if (rehla != null) {
            agenceComboBox.setValue(rehla.getAgence());
            departTextField.setText(rehla.getDepart());
            destinationTextField.setText(rehla.getDestination());
            departDatePicker.setValue(rehla.getDepart_date().toLocalDate());
            departTimeComboBox.setValue(rehla.getDepart_date().toLocalTime().withSecond(0).withNano(0));
            arrivalDatePicker.setValue(rehla.getArrival_date().toLocalDate());
            arrivalTimeComboBox.setValue(rehla.getArrival_date().toLocalTime().withSecond(0).withNano(0));
            priceTextField.setText(String.valueOf(rehla.getPrice()));
        }
    }

    public void setListRehlaController(ListRehlaController controller) {
        this.listRehlaController = controller;
    }

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    @FXML
    private void handleSave() {
        if (rehlaToEdit != null) {
            compagnie_aerienne selectedAgence = agenceComboBox.getValue();
            String depart = departTextField.getText();
            String destination = destinationTextField.getText();
            java.time.LocalDate departDate = departDatePicker.getValue();
            LocalTime departTime = departTimeComboBox.getValue();
            java.time.LocalDate arrivalDate = arrivalDatePicker.getValue();
            LocalTime arrivalTime = arrivalTimeComboBox.getValue();
            String priceText = priceTextField.getText();

            if (selectedAgence == null || depart == null || depart.isEmpty() || destination == null || destination.isEmpty() ||
                    departDate == null || departTime == null || arrivalDate == null || arrivalTime == null || priceText == null || priceText.isEmpty()) {
                showAlert("Veuillez remplir tous les champs.");
                return;
            }

            try {
                float price = Float.parseFloat(priceText);
                LocalDateTime departDateTime = LocalDateTime.of(departDate, departTime);
                LocalDateTime arrivalDateTime = LocalDateTime.of(arrivalDate, arrivalTime);

                rehlaToEdit.setAgence(selectedAgence);
                rehlaToEdit.setDepart(depart);
                rehlaToEdit.setDestination(destination);
                rehlaToEdit.setDepart_date(departDateTime);
                rehlaToEdit.setArrival_date(arrivalDateTime);
                rehlaToEdit.setPrice(price);

                rehlaService.update(rehlaToEdit);
                showAlert("Vol mis à jour avec succès.");
                if (listRehlaController != null) {
                    listRehlaController.loadRehlas();
                }
                closeDialog();
            } catch (NumberFormatException e) {
                showAlert("Le prix doit être un nombre valide.");
            } catch (Exception e) {
                showAlert("Une erreur s'est produite lors de la mise à jour du vol.");
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    @FXML
    private void handleConfirm() {
        if (rehlaToEdit != null) {
            compagnie_aerienne selectedAgence = agenceComboBox.getValue();
            String depart = departTextField.getText();
            String destination = destinationTextField.getText();
            java.time.LocalDate departDate = departDatePicker.getValue();
            LocalTime departTime = departTimeComboBox.getValue();
            java.time.LocalDate arrivalDate = arrivalDatePicker.getValue();
            LocalTime arrivalTime = arrivalTimeComboBox.getValue();
            String priceText = priceTextField.getText();

            if (selectedAgence == null || depart == null || depart.isEmpty() || destination == null || destination.isEmpty() ||
                    departDate == null || departTime == null || arrivalDate == null || arrivalTime == null || priceText == null || priceText.isEmpty()) {
                showAlert("Veuillez remplir tous les champs.");
                return;
            }

            try {
                float price = Float.parseFloat(priceText);
                LocalDateTime departDateTime = LocalDateTime.of(departDate, departTime);
                LocalDateTime arrivalDateTime = LocalDateTime.of(arrivalDate, arrivalTime);

                rehlaToEdit.setAgence(selectedAgence);
                rehlaToEdit.setDepart(depart);
                rehlaToEdit.setDestination(destination);
                rehlaToEdit.setDepart_date(departDateTime);
                rehlaToEdit.setArrival_date(arrivalDateTime);
                rehlaToEdit.setPrice(price);

                showAlert("Modifications appliquées. Cliquez sur 'Enregistrer' pour sauvegarder.");

            } catch (NumberFormatException e) {
                showAlert("Le prix doit être un nombre valide.");
            }
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    // Non-standard method for the controller to load its own FXML
    public static updaterehlacontroller loadFXML() throws IOException {
        FXMLLoader loader = new FXMLLoader(updaterehlacontroller.class.getResource("/views/UpdateRehlaForm.fxml"));
        Parent root = loader.load();
        updaterehlacontroller controller = loader.getController();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
        controller.setDialogStage(stage); // Set the stage in the controller
        return controller;
    }
}