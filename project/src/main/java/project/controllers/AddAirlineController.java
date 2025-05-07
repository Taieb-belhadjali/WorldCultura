package project.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import project.models.aminemodels.compagnie_aerienne;
import project.service.amineservice.compagnie_areienneservice;

import java.io.File;

public class AddAirlineController {

    @FXML
    private TextField nomField;
    @FXML
    private TextField logoField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private TextField contactField;
    @FXML
    private ImageView logoPreview; // Pour l'aperçu de l'image sélectionnée
    @FXML
    private Button chooseLogoButton; // Le bouton pour choisir le fichier

    private compagnie_areienneservice service = new compagnie_areienneservice();
    private compagnie_aerienne existingAirline; // null si ajout
    private File selectedLogoFile; // Stocke le fichier logo sélectionné

    public void setAirlineToEdit(compagnie_aerienne compagnie) {
        this.existingAirline = compagnie;
        if (compagnie != null) {
            nomField.setText(compagnie.getNom());
            logoField.setText(compagnie.getLogo());
            // Tentative d'afficher l'image existante pour la modification
            loadLogo(compagnie.getLogo());
            descriptionField.setText(compagnie.getDescription());
            contactField.setText(compagnie.getContact_du_responsable());
        }
    }

    @FXML
    private void handleChooseLogo(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir le logo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        selectedLogoFile = fileChooser.showOpenDialog(((Button) event.getSource()).getScene().getWindow());

        if (selectedLogoFile != null) {
            logoField.setText(selectedLogoFile.getAbsolutePath());
            loadLogo(selectedLogoFile.getAbsolutePath());
        }
    }

    private void loadLogo(String imagePath) {
        try {
            if (imagePath != null && !imagePath.trim().isEmpty()) {
                Image image;
                if (imagePath.startsWith("images/")) {
                    image = new Image(getClass().getResourceAsStream("/" + imagePath));
                } else {
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        image = new Image(imageFile.toURI().toString());
                    } else {
                        System.out.println("Image introuvable: " + imageFile.getAbsolutePath());
                        image = new Image(getClass().getResourceAsStream("/images/image-placeholder.png")); // Utilisez une image par défaut si non trouvée dans le système de fichiers absolu
                    }
                }
                logoPreview.setImage(image);
            } else {
                logoPreview.setImage(new Image(getClass().getResourceAsStream("/images/image-placeholder.png"))); // Image par défaut si aucun chemin
            }
        } catch (Exception e) {
            System.err.println("Erreur de chargement de l'image: " + e.getMessage());
            logoPreview.setImage(new Image(getClass().getResourceAsStream("/images/image-placeholder.png"))); // Image par défaut en cas d'erreur
        }
    }

    @FXML
    private void handleSave() {
        String nom = nomField.getText();
        String logo = logoField.getText(); // Maintenant contient le chemin sélectionné
        String description = descriptionField.getText();
        String contact = contactField.getText();

        if (existingAirline == null) {
            compagnie_aerienne nouvelle = new compagnie_aerienne();
            nouvelle.setNom(nom);
            nouvelle.setLogo(logo);
            nouvelle.setDescription(description);
            nouvelle.setContact_du_responsable(contact);
            service.add(nouvelle);
        } else {
            existingAirline.setNom(nom);
            existingAirline.setLogo(logo);
            existingAirline.setDescription(description);
            existingAirline.setContact_du_responsable(contact);
            service.update(existingAirline);
        }

        ((Stage) nomField.getScene().getWindow()).close();
    }

    @FXML
    private void handleCancel() {
        ((Stage) nomField.getScene().getWindow()).close();
    }
}