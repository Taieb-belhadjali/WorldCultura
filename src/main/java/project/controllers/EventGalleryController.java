package project.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import project.models.event;
import project.service.eventservice;

import java.io.IOException;
import java.util.List;

public class EventGalleryController {
    @FXML
    private FlowPane eventContainer;

    private final eventservice es = new eventservice();

    public void initialize() {
        List<event> events = es.getAll();
        for (event e : events) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/EventItem.fxml"));
                VBox eventBox = loader.load();
                EventItemController controller = loader.getController();
                controller.setData(e);
                eventContainer.getChildren().add(eventBox);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
