package project.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import project.models.event;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import project.models.event;
import java.io.IOException;






import java.io.File;

public class EventItemController {
    @FXML private ImageView eventImage;
    @FXML private Label eventName;
    @FXML private Button detailButton;
    @FXML private VBox eventBox;
    @FXML private Button participerButton;
    @FXML
    private void handleDetails(ActionEvent event) {
        // Logique pour afficher les détails de l’événement (ouvrir une popup, une autre scène, etc.)
        System.out.println("➡️ Bouton Détails cliqué !");
    }


    private event currentEvent;

    public void setData(event e) {
        this.currentEvent = e;
        eventName.setText(e.getName());

        File file = new File(e.getImage());
        if (file.exists()) {
            eventImage.setImage(new Image(file.toURI().toString()));
        }

        detailButton.setOnAction(evt -> {
            // 👉 Ici on ouvrira une vue de détails
            EventDetailPopup.show(currentEvent);
        });
    }
    @FXML
    private void handleParticiper(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddParticipation.fxml"));
            Scene scene = new Scene(loader.load());

            // Passage de l’événement sélectionné au contrôleur de participation
            AddParticipationController controller = loader.getController();
            controller.setSelectedEvent(currentEvent);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Participer à l'événement");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
