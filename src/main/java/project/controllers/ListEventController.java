
package project.controllers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import project.controllers.UpdateEventController;
import project.models.event;

import project.service.eventservice;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import project.service.eventservice;
import javafx.event.ActionEvent;


public class ListEventController implements Initializable {

    @FXML
    private TableView<event> eventsTable;
    @FXML
    private TableColumn<event, Integer> idColumn;
    @FXML
    private TableColumn<event, String> nameColumn;
    @FXML
    private TableColumn<event, String> descriptionColumn;
    @FXML
    private TableColumn<event, String> dateDebutColumn;
    @FXML
    private TableColumn<event, String> dateFinColumn;
    @FXML
    private TableColumn<event, String> imageColumn;
    @FXML
    private TableColumn<event, Void> actionColumn;

    private final eventservice serviceEvent = new eventservice();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<event> eventList = serviceEvent.getAll();
        ObservableList<event> observableEvents = FXCollections.observableArrayList(eventList);
        eventsTable.setItems(observableEvents);


        nameColumn.setCellValueFactory(new PropertyValueFactory<>("Name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("Description"));
        dateDebutColumn.setCellValueFactory(new PropertyValueFactory<>("date_debut"));
        dateFinColumn.setCellValueFactory(new PropertyValueFactory<>("date_fin"));
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("image"));

        imageColumn.setCellFactory(column -> new TableCell<event, String>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitWidth(100);
                imageView.setFitHeight(70);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);
                if (empty || imagePath == null || imagePath.isEmpty()) {
                    setGraphic(null);
                } else {
                    try {
                        imageView.setImage(new Image("file:" + imagePath));
                        setGraphic(imageView);
                    } catch (Exception e) {
                        setGraphic(null);
                    }
                }
            }
        });

        actionColumn.setCellFactory(param -> new TableCell<event, Void>() {
            private final Button btnModifier = new Button("Modifier");
            private final Button btnSupprimer = new Button("Supprimer");

            {
                btnModifier.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;");
                btnSupprimer.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;");

                btnModifier.setOnAction(event -> {
                    event e = getTableView().getItems().get(getIndex());
                    openModifierForm(e);
                });

                btnSupprimer.setOnAction(event -> {
                    event e = getTableView().getItems().get(getIndex());
                    deleteEvent(e);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(10, btnModifier, btnSupprimer);
                    setGraphic(hbox);
                }
            }
        });
    }

    private void openModifierForm(event e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UpdateEvent.fxml"));
            Parent root = loader.load();
            UpdateEventController controller = loader.getController();
            controller.setEvent(e);
            Stage stage = (Stage) eventsTable.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteEvent(event e) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer cet événement ?");
        confirm.setContentText("Êtes-vous sûr de vouloir le supprimer ?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                serviceEvent.delete(e.getID());
                eventsTable.getItems().remove(e);
            }
        });
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    private void ajouterEvent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListEvents.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) eventsTable.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleAddEvent(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddEvent.fxml")); // mets le bon chemin
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un événement");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger la page d'ajout.");
            e.printStackTrace();
        }
    }

}
