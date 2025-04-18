package project.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import project.models.BonPlan;
import project.service.BonPlanService;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AjoutBonPlanController {

    @FXML
    private TextField titreTextField;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private TextField lieuTextField;

    @FXML
    private DatePicker dateExpirationDatePicker;

    @FXML
    private ComboBox<String> categorieComboBox;

    private BonPlanService bonPlanService = new BonPlanService();

    @FXML
    public void initialize() {
        // Initialize the categories in the ComboBox
        categorieComboBox.getItems().addAll("Voyages", "Hébergement", "Loisirs", "Événements", "Restaurants", "Shopping", "Autres");
        categorieComboBox.setValue("Autres"); // Set a default value
    }

    @FXML
    public void ajouterBonPlan(ActionEvent event) {
        String titre = titreTextField.getText();
        String description = descriptionTextArea.getText();
        String lieu = lieuTextField.getText();
        LocalDate dateExpiration = dateExpirationDatePicker.getValue();
        String categorie = categorieComboBox.getValue();
        LocalDateTime dateCreation = LocalDateTime.now(); // Set the creation date to now

        if (titre.isEmpty() || description.isEmpty() || lieu.isEmpty() || dateExpiration == null || categorie == null) {
            // Optionally display an error message to the user
            System.out.println("Veuillez remplir tous les champs.");
            return;
        }

        BonPlan nouveauBonPlan = new BonPlan(titre, description, lieu, dateCreation, dateExpiration, categorie);
        bonPlanService.add(nouveauBonPlan);

        // Optionally display a success message and clear the form
        System.out.println("Bon plan ajouté avec succès: " + nouveauBonPlan);
        clearForm();
    }

    @FXML
    public void clearForm() {
        titreTextField.clear();
        descriptionTextArea.clear();
        lieuTextField.clear();
        dateExpirationDatePicker.setValue(null);
        categorieComboBox.setValue("Autres");
    }
}
