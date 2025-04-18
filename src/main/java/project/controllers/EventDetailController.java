package project.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import project.models.event;

import java.io.File;

public class EventDetailController {

    @FXML private ImageView eventImage;
    @FXML private Label eventName;
    @FXML private Label eventDateDebut;
    @FXML private Label eventDateFin;
    @FXML private TextArea eventDescription;

    private Stage stage;

    public void setData(event e, Stage stage) {
        this.stage = stage;
        eventName.setText(e.getName());
        eventDateDebut.setText("Date début : " + e.getDate_debut());
        eventDateFin.setText("Date fin : " + e.getDate_fin());
        eventDescription.setText(e.getDescription());

        File file = new File(e.getImage());
        if (file.exists()) {
            eventImage.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    private void handleClose() {
        if (stage != null) {
            stage.close();
        }
    }
}
