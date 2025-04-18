package project.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import project.models.compagnie_aerienne;
import project.service.compagnie_areienneservice;

import java.io.File;

public class updateairlinecontroller {

    @FXML
    private TextField idTextField;

    @FXML
    private TextField nomTextField;

    @FXML
    private TextField logoTextField;

    @FXML
    private ImageView logoPreview; // Reference to the ImageView

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private TextField contactTextField;

    @FXML
    private Button updateButton;

    private compagnie_aerienne airlineToEdit;
    private final compagnie_areienneservice airlineService = new compagnie_areienneservice();
    private String selectedLogoPath; // To store the path of the chosen file

    public void setAirlineToEdit(compagnie_aerienne airline) {
        this.airlineToEdit = airline;
        if (airlineToEdit != null) {
            idTextField.setText(String.valueOf(airlineToEdit.getId()));
            nomTextField.setText(airlineToEdit.getNom());
            logoTextField.setText(airlineToEdit.getLogo());
            // Load the existing logo into the preview if it's a valid URL/path
            if (airlineToEdit.getLogo() != null && !airlineToEdit.getLogo().isEmpty()) {
                try {
                    Image image = new Image(airlineToEdit.getLogo());
                    logoPreview.setImage(image);
                } catch (Exception e) {
                    System.err.println("Could not load existing logo: " + airlineToEdit.getLogo());
                    logoPreview.setImage(null); // Clear preview on error
                }
            } else {
                logoPreview.setImage(null);
            }
            descriptionTextArea.setText(airlineToEdit.getDescription());
            contactTextField.setText(airlineToEdit.getContact_du_responsable());
        }
        selectedLogoPath = airlineToEdit != null ? airlineToEdit.getLogo() : null; // Initialize with existing logo
    }

    @FXML
    void chooseLogoFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir le nouveau logo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(((Button) event.getSource()).getScene().getWindow());

        if (selectedFile != null) {
            selectedLogoPath = selectedFile.getAbsolutePath();
            logoTextField.setText(selectedLogoPath);
            try {
                Image image = new Image(selectedFile.toURI().toString());
                logoPreview.setImage(image);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger l'image sélectionnée.");
                logoPreview.setImage(null);
                selectedLogoPath = null;
                logoTextField.clear();
            }
        }
    }

    @FXML
    void updateAirline(ActionEvent event) {
        if (airlineToEdit == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucune compagnie sélectionnée pour la mise à jour.");
            return;
        }

        String nom = nomTextField.getText();
        String logo = selectedLogoPath != null ? selectedLogoPath : logoTextField.getText(); // Use selected file path if available
        String description = descriptionTextArea.getText();
        String contact = contactTextField.getText();

        if (nom.isEmpty() || logo.isEmpty() || description.isEmpty() || contact.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        airlineToEdit.setNom(nom);
        airlineToEdit.setLogo(logo); // Store the file path
        airlineToEdit.setDescription(description);
        airlineToEdit.setContact_du_responsable(contact);
        airlineService.update(airlineToEdit);
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Compagnie aérienne mise à jour avec succès.");
        clearFields();
        Stage stage = (Stage) updateButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearFields() {
        nomTextField.clear();
        logoTextField.clear();
        logoPreview.setImage(null); // Clear the preview
        descriptionTextArea.clear();
        contactTextField.clear();
        selectedLogoPath = null; // Reset the selected path
    }
}