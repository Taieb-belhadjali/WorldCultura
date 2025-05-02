package project.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class SidebarController {

    public void loadAirlines(ActionEvent event) throws IOException {
        loadView("views/ListAirlines.fxml", event);
    }

    public void loadFlights(ActionEvent event) throws IOException {
        loadView("views/ListRehla.fxml", event);
    }

    public void loadReservations(ActionEvent event) throws IOException {
        loadView("views/list_reservations.fxml", event);
    }

    private void loadView(String fxmlPath, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlPath)); // Assure-toi que le chemin est correct
        Node view = loader.load();

        // Trouver le BorderPane principal (si c'est ta structure) et centrer la nouvelle vue
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        BorderPane root = (BorderPane) stage.getScene().getRoot();
        root.setCenter(view);

        // Si tu as une autre structure, adapte cette partie pour afficher la vue
        // Par exemple, si tu utilises un StackPane :
        // StackPane root = (StackPane) stage.getScene().getRoot();
        // root.getChildren().setAll(view);
    }
}