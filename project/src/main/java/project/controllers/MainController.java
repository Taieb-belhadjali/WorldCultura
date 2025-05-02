package project.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {

    @FXML
    private BorderPane mainRootPane;

    @FXML
    private SidebarController sidebarController; // Référence au contrôleur de la barre latérale (si nécessaire)

    @FXML
    public void initialize() {
        // Charger la vue initiale au démarrage (par exemple, la liste des compagnies aériennes)
        loadView("ListAirlines.fxml");
    }

    public void loadAirlines(ActionEvent event) {
        loadView("ListAirlines.fxml");
    }

    public void loadFlights(ActionEvent event) {
        loadView("ListRehla.fxml");
    }

    public void loadReservations(ActionEvent event) {
        loadView("list_reservations.fxml");
    }

    // MODIFICATION ICI : Rendre la méthode loadView publique
    public void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/" + fxmlPath));
            Node view = loader.load();
            mainRootPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
            // Gérer l'erreur de chargement de la vue
        }
    }
}