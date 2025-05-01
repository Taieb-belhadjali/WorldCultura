package project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main5 extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Charger le fichier FXML de la page de réservation utilisateur
            Parent root = FXMLLoader.load(getClass().getResource("/views/reservationuser.fxml"));

            // Créer la scène
            Scene scene = new Scene(root);

            // Configurer le titre de la fenêtre
            primaryStage.setTitle("Mes Réservations");

            // Ajouter la scène à la fenêtre
            primaryStage.setScene(scene);

            // Afficher la fenêtre
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // Gérer l'erreur de chargement du fichier FXML ici
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}