package project.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import project.models.rehla;
import project.service.rehlaservice;

public class deleterehlacontroller {

    @FXML
    private Label confirmationLabel;

    @FXML
    private Button confirmButton;

    @FXML
    private Button cancelButton;

    private rehla rehlaToDelete;
    private final rehlaservice rehlaService = new rehlaservice();
    private ListRehlaController listRehlaController;

    public void setRehlaToDelete(rehla rehla) {
        this.rehlaToDelete = rehla;
        if (rehla != null) {
            confirmationLabel.setText("Êtes-vous sûr de vouloir supprimer le vol ID " + rehla.getId() + "?");
        } else {
            confirmationLabel.setText("Erreur: Aucun vol à supprimer.");
            confirmButton.setDisable(true);
        }
    }

    public void setListRehlaController(ListRehlaController controller) {
        this.listRehlaController = controller;
    }

    @FXML
    private void handleConfirmDelete() {
        if (rehlaToDelete != null) {
            rehlaService.delete(rehlaToDelete.getId());
            if (listRehlaController != null) {
                listRehlaController.loadRehlas();
            }
            closeDialog();
        }
    }

    @FXML
    private void handleCancelDelete() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}