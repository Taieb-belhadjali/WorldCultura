package project.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import project.models.event;
import project.service.eventservice;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import java.io.IOException;




import java.io.File;

public class AddEventController {

    @FXML private TextField nameField;
    @FXML private TextField startDateField;
    @FXML private TextField endDateField;
    @FXML private TextField descriptionField;
    @FXML private TextField imageField;
    @FXML private Button chooseImageButton;
    @FXML private ImageView imagePreview;

    private final eventservice service = new eventservice();
    private String selectedImagePath = null;

    @FXML
    public void handleAddEvent() {
        String name = nameField.getText();
        String start = startDateField.getText();
        String end = endDateField.getText();
        String desc = descriptionField.getText();
        String image = selectedImagePath != null ? selectedImagePath : "";

        event e = new event(name, start, end, desc, image);
        service.add(e);
        clearFields();
    }

    @FXML
    public void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            selectedImagePath = selectedFile.getAbsolutePath();
            imageField.setText(selectedImagePath);
            imagePreview.setImage(new Image("file:" + selectedImagePath));
        }
    }

    private void clearFields() {
        nameField.clear();
        startDateField.clear();
        endDateField.clear();
        descriptionField.clear();
        imageField.clear();
        imagePreview.setImage(null);
        selectedImagePath = null;
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void handleBackToList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ListEvents.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène et l'afficher
            Stage stage = (Stage) nameField.getScene().getWindow();  // ou un autre élément de la scène
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des événements");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger l'interface de la liste.");
        }
    }


}
