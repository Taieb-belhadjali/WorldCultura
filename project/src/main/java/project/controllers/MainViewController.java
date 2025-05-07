package project.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class MainViewController {

    @FXML
    private BorderPane mainPane;

    public void loadView(String fxmlPath) {
        try {
            Pane view = FXMLLoader.load(getClass().getResource("/" + fxmlPath));
            mainPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
            // Gérer l'erreur de chargement de la vue
        }
    }
}