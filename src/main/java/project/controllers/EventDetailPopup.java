package project.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import project.models.event;

import java.io.IOException;

public class EventDetailPopup {

    public static void show(event e) {
        try {
            FXMLLoader loader = new FXMLLoader(EventDetailPopup.class.getResource("/views/EventDetail.fxml"));
            VBox root = loader.load();

            EventDetailController controller = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Détails de l'événement");
            stage.setScene(new Scene(root));
            controller.setData(e, stage);

            stage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
