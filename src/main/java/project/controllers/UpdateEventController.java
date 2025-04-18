package project.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import project.models.event;
import project.service.eventservice;

import java.io.File;

public class UpdateEventController {

    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField startDateField;
    @FXML private TextField endDateField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField imageField;  // ✅ Corrigé : ce champ est bien lié au FXML
    @FXML private ImageView imagePreview;
    @FXML private Label imagePathLabel;
    @FXML private Button chooseImageButton;

    private final eventservice service = new eventservice();
    private String selectedImagePath;

    @FXML
    private void updateEvent() {
        try {
            if (nameField.getText().isEmpty() || startDateField.getText().isEmpty() || endDateField.getText().isEmpty() || descriptionArea.getText().isEmpty()) {
                showAlert("Erreur", "Tous les champs doivent être remplis.");
                return;
            }

            String start = startDateField.getText();
            String end = endDateField.getText();
            if (!isValidDate(start) || !isValidDate(end)) {
                showAlert("Erreur", "Les dates doivent être valides au format YYYY-MM-DD.");
                return;
            }

            int id = Integer.parseInt(idField.getText());
            String name = nameField.getText();
            String description = descriptionArea.getText();
            String image = selectedImagePath != null ? selectedImagePath : imageField.getText();

            event updatedEvent = new event(id, name, start, end, description, image);
            service.update(updatedEvent);

            showAlert("Succès", "L'événement a été mis à jour avec succès!");
            resetFields();

        } catch (NumberFormatException e) {
            showAlert("Erreur", "Format d'ID invalide.");
        } catch (Exception e) {
            e.printStackTrace(); // Utile pour le débogage
            showAlert("Erreur", "Une erreur s'est produite lors de la mise à jour.");
        }
    }

    private boolean isValidDate(String date) {
        return date.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void resetFields() {
        idField.clear();
        nameField.clear();
        startDateField.clear();
        endDateField.clear();
        descriptionArea.clear();
        imageField.clear();
        imagePreview.setImage(null);
        imagePathLabel.setText("");
        selectedImagePath = null;
    }

    @FXML
    private void handleImageImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image pour l'événement");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichiers image", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            selectedImagePath = selectedFile.toURI().toString();
            imageField.setText(selectedImagePath);  // ✅ imageField bien initialisé maintenant
            imagePathLabel.setText(selectedFile.getName());
            imagePreview.setImage(new Image(selectedImagePath));
        }
    }

    public void setEvent(event e) {
        idField.setText(String.valueOf(e.getID()));
        nameField.setText(e.getName());
        startDateField.setText(e.getDate_debut());
        endDateField.setText(e.getDate_fin());
        descriptionArea.setText(e.getDescription());
        imageField.setText(e.getImage());

        if (e.getImage() != null && !e.getImage().isEmpty()) {
            try {
                Image img = new Image(e.getImage());
                imagePreview.setImage(img);
                imagePathLabel.setText(new File(e.getImage()).getName());
            } catch (Exception ex) {
                System.err.println("⚠️ Impossible de charger l'image.");
            }
        }
    }
    @FXML
    private void handleReturnToList() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/views/ListEvents.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.scene.Scene scene = new javafx.scene.Scene(root);

            // Obtenir la fenêtre actuelle
            javafx.stage.Stage stage = (javafx.stage.Stage) idField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Liste des événements");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger l'interface de la liste.");
        }
    }

}
