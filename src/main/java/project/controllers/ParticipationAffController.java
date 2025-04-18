package project.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import project.models.participation;
import project.service.serviceparticipation;

import java.io.IOException;
import java.util.List;

public class ParticipationAffController {

    @FXML
    private TableView<participation> tableParticipations;
    @FXML
    private TableColumn<participation, Integer> colId;
    @FXML
    private TableColumn<participation, String> colNom;
    @FXML
    private TableColumn<participation, String> colEmail;
    @FXML
    private TableColumn<participation, Integer> colTelephone;
    @FXML
    private TableColumn<participation, Void> colUpdate;
    @FXML
    private TableColumn<participation, Void> colDelete;

    private serviceparticipation service = new serviceparticipation();

    // Initialisation
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));

        addButtonToUpdate();
        addButtonToDelete();

        loadParticipations();
    }

    // Chargement des participations
    private void loadParticipations() {
        List<participation> participations = service.getAll();
        tableParticipations.getItems().setAll(participations);
    }

    // Ajouter le bouton "Modifier" à chaque ligne
    private void addButtonToUpdate() {
        Callback<TableColumn<participation, Void>, TableCell<participation, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btn = new Button("Modifier");

            {
                btn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-cursor: hand;");
                btn.setOnAction(event -> {
                    participation selected = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UpdateParticipation.fxml"));
                        Parent root = loader.load();
                        UpdateParticipationController controller = loader.getController();
                        controller.initData(selected);

                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Modifier la participation");
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        showAlert("Erreur", "Impossible d'ouvrir la vue de mise à jour.", Alert.AlertType.ERROR);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        };

        colUpdate.setCellFactory(cellFactory);
    }

    // Ajouter le bouton "Supprimer" à chaque ligne
    private void addButtonToDelete() {
        Callback<TableColumn<participation, Void>, TableCell<participation, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btn = new Button("Supprimer");

            {
                btn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-cursor: hand;");
                btn.setOnAction(event -> {
                    participation selected = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Confirmer la suppression ?", ButtonType.YES, ButtonType.NO);
                    confirm.setHeaderText(null);
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            service.delete(selected.getId());
                            loadParticipations(); // Rafraîchir la table
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        };

        colDelete.setCellFactory(cellFactory);
    }

    // Méthode pour afficher une alerte générique
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
