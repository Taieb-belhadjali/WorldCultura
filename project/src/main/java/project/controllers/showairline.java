package project.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import project.models.aminemodels.compagnie_aerienne;

import java.io.File;

public class showairline {

    @FXML
    private Label idLabel;

    @FXML
    private Label nomLabel;

    @FXML
    private ImageView logoImageView; // Utilisez un ImageView pour afficher l'image du logo

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label contactLabel;

    private compagnie_aerienne airline;

    public void setAirline(compagnie_aerienne airline) {
        this.airline = airline;
        if (airline != null) {
            idLabel.setText(String.valueOf(airline.getId()));
            nomLabel.setText(airline.getNom());
            loadLogoImage(airline); // Appeler la méthode pour charger le logo
            descriptionLabel.setText(airline.getDescription());
            contactLabel.setText(airline.getContact_du_responsable());
        } else {
            clearLabels();
            // Optionally clear the ImageView as well
            logoImageView.setImage(null);
        }
    }

    private void loadLogoImage(compagnie_aerienne airline) {
        try {
            String imagePath = airline.getLogo(); // Récupérer le chemin du logo depuis l'objet compagnie_aerienne
            if (imagePath != null && !imagePath.trim().isEmpty()) {
                if (imagePath.startsWith("images/")) {
                    Image image = new Image(getClass().getResourceAsStream("/" + imagePath));
                    logoImageView.setImage(image);
                } else {
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        Image image = new Image(imageFile.toURI().toString());
                        logoImageView.setImage(image);
                    } else {
                        System.out.println("Logo introuvable: " + imageFile.getAbsolutePath());
                        // Charger une image de remplacement si le logo n'est pas trouvé
                        Image placeholderImage = new Image(getClass().getResourceAsStream("/images/airline-placeholder.png"));
                        logoImageView.setImage(placeholderImage);
                    }
                }
            } else {
                // Charger une image de remplacement si le chemin du logo est vide ou null
                Image placeholderImage = new Image(getClass().getResourceAsStream("/images/airline-placeholder.png"));
                logoImageView.setImage(placeholderImage);
            }
        } catch (Exception e) {
            System.err.println("Erreur de chargement du logo: " + e.getMessage());
            // Charger une image de remplacement en cas d'erreur
            Image placeholderImage = new Image(getClass().getResourceAsStream("/images/airline-placeholder.png"));
            logoImageView.setImage(placeholderImage);
        }
    }

    private void clearLabels() {
        idLabel.setText("");
        nomLabel.setText("");
        descriptionLabel.setText("");
        contactLabel.setText("");
    }
}