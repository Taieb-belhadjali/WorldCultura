package project;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import project.controllers.MainViewController;
import project.controllers.SidebarController;


public class Backamine extends Application {

    @FXML
    private BorderPane mainPane;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/views/amineviews/main_view.fxml"));
        BorderPane root = mainLoader.load();

        MainViewController mainViewController = mainLoader.getController();

        FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("/views/amineviews/sidebar.fxml"));
        VBox sidebar = sidebarLoader.load();
        SidebarController sidebarController = sidebarLoader.getController();

        // Liaison des contrôleurs
        sidebarController.setMainViewController(mainViewController);

        root.setLeft(sidebar);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles/styleamine.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("World Cultura");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}