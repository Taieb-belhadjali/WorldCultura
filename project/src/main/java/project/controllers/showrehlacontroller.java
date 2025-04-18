package project.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import project.models.rehla;
import project.service.rehlaservice;

import java.time.format.DateTimeFormatter;

public class showrehlacontroller {

    @FXML
    private Label idLabel;

    @FXML
    private Label agenceLabel;

    @FXML
    private Label departLabel;

    @FXML
    private Label destinationLabel;

    @FXML
    private Label departDateLabel;

    @FXML
    private Label arrivalDateLabel;

    @FXML
    private Label priceLabel;

    private rehla rehla;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final rehlaservice rehlaService = new rehlaservice();

    public void setRehla(rehla rehla) {
        this.rehla = rehla;
        if (rehla != null) {
            idLabel.setText(String.valueOf(rehla.getId()));
            agenceLabel.setText(rehla.getAgence().getNom());
            departLabel.setText(rehla.getDepart());
            destinationLabel.setText(rehla.getDestination());
            departDateLabel.setText(rehla.getDepart_date().format(dateFormatter));
            arrivalDateLabel.setText(rehla.getArrival_date().format(dateFormatter));
            priceLabel.setText(String.valueOf(rehla.getPrice()));
        } else {
            // Handle the case where the rehla object is null
            idLabel.setText("");
            agenceLabel.setText("");
            departLabel.setText("");
            destinationLabel.setText("");
            departDateLabel.setText("");
            arrivalDateLabel.setText("");
            priceLabel.setText("");
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) idLabel.getScene().getWindow();
        stage.close();
    }
}