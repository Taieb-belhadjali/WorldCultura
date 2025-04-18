package project.controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import project.models.compagnie_aerienne;
import project.service.compagnie_areienneservice;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

public class ListAirlinesController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<compagnie_aerienne> airlineTable;

    @FXML
    private TableColumn<compagnie_aerienne, Integer> idColumn;

    @FXML
    private TableColumn<compagnie_aerienne, String> nomColumn;

    @FXML
    private TableColumn<compagnie_aerienne, ImageView> logoColumn; // Changed to ImageView

    @FXML
    private TableColumn<compagnie_aerienne, String> descriptionColumn;

    @FXML
    private TableColumn<compagnie_aerienne, String> contactColumn;

    @FXML
    private TableColumn<compagnie_aerienne, Void> actionColumn;

    private final compagnie_areienneservice service = new compagnie_areienneservice();
    private final ObservableList<compagnie_aerienne> airlineList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        nomColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNom()));
        descriptionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));
        contactColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getContact_du_responsable()));

        // Set the cell value factory for the logo column
        logoColumn.setCellValueFactory(data -> {
            String imagePath = data.getValue().getLogo();
            System.out.println("Logo Path for " + data.getValue().getNom() + ": " + imagePath); // DEBUGGING
            ImageView imageView = new ImageView();
            if (imagePath != null && !imagePath.trim().isEmpty()) {
                try {
                    // Si le chemin est relatif (commence par "images/")
                    if (imagePath.startsWith("images/")) {
                        InputStream inputStream = getClass().getResourceAsStream("/" + imagePath);
                        if (inputStream != null) {
                            imageView.setImage(new Image(inputStream));
                            try {
                                inputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            System.err.println("Image introuvable dans les ressources: " + imagePath);
                            // Optionally set a placeholder image
                            // imageView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
                        }
                    }
                    // Si c'est un chemin absolu
                    else {
                        File imageFile = new File(imagePath);
                        if (imageFile.exists()) {
                            imageView.setImage(new Image(imageFile.toURI().toString()));
                        } else {
                            System.out.println("Image introuvable: " + imageFile.getAbsolutePath());
                            // Optionally set a placeholder image
                            // imageView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
                        }
                    }
                    imageView.setFitWidth(50);
                    imageView.setFitHeight(50);
                    imageView.setPreserveRatio(true);
                } catch (Exception e) {
                    System.err.println("Erreur de chargement de l'image: " + e.getMessage());
                    // Optionally set a placeholder image
                    // imageView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
                }
            } else {
                // Optionally set a placeholder image if no logo path is available
                // imageView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
                System.out.println("Chemin du logo nul ou vide pour " + data.getValue().getNom());
            }
            return new javafx.beans.property.SimpleObjectProperty<>(imageView);
        });

        // Initialize action column with buttons
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Supprimer");
            private final Button updateButton = new Button("Modifier");
            private final Button detailsButton = new Button("Détails");
            private final HBox buttonBox = new HBox(deleteButton, updateButton, detailsButton);

            {
                buttonBox.setSpacing(5);
                deleteButton.setOnAction(event -> {
                    compagnie_aerienne airline = getTableRow().getItem();
                    if (airline != null) {
                        handleDeleteAirline(airline.getId());
                    }
                });
                updateButton.setOnAction(event -> {
                    compagnie_aerienne airline = getTableRow().getItem();
                    if (airline != null) {
                        handleUpdateAirlineForm(airline);
                    }
                });
                detailsButton.setOnAction(event -> {
                    compagnie_aerienne airline = getTableRow().getItem();
                    if (airline != null) {
                        handleShowAirlineDetails(airline);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonBox);
                }
            }
        });

        loadAirlines();

        searchField.setOnKeyReleased(this::handleSearch);
    }

    private void loadAirlines() {
        List<compagnie_aerienne> compagnies = service.getAll();
        airlineList.setAll(compagnies);
        airlineTable.setItems(airlineList);
    }

    private void handleSearch(KeyEvent event) {
        String keyword = searchField.getText().toLowerCase();
        List<compagnie_aerienne> filtered = airlineList.stream()
                .filter(c -> c.getNom().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
        airlineTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void handleAddAirline() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddAirlineForm.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter une compagnie");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadAirlines();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteAirline(int id) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette compagnie ?");
        alert.setContentText("ID de la compagnie : " + id);

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                service.delete(id);
                loadAirlines();
            }
        });
    }

    @FXML
    private void handleDeleteSelectedAirline() {
        compagnie_aerienne selectedAirline = airlineTable.getSelectionModel().getSelectedItem();
        if (selectedAirline != null) {
            handleDeleteAirline(selectedAirline.getId());
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Avertissement");
            alert.setHeaderText("Aucune compagnie sélectionnée");
            alert.setContentText("Veuillez sélectionner une compagnie dans la liste pour la supprimer.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleUpdateAirline() {
        compagnie_aerienne selectedAirline = airlineTable.getSelectionModel().getSelectedItem();
        if (selectedAirline != null) {
            handleUpdateAirlineForm(selectedAirline);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Avertissement");
            alert.setHeaderText("Aucune compagnie sélectionnée");
            alert.setContentText("Veuillez sélectionner une compagnie dans la liste pour la modifier.");
            alert.showAndWait();
        }
    }

    private void handleUpdateAirlineForm(compagnie_aerienne airline) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/update.fxml"));
            Parent root = loader.load();

            updateairlinecontroller controller = loader.getController();
            controller.setAirlineToEdit(airline);

            Stage stage = new Stage();
            stage.setTitle("Modifier la compagnie");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadAirlines();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleShowSelectedAirlineDetails() {
        compagnie_aerienne selectedAirline = airlineTable.getSelectionModel().getSelectedItem();
        if (selectedAirline != null) {
            handleShowAirlineDetails(selectedAirline);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Avertissement");
            alert.setHeaderText("Aucune compagnie sélectionnée");
            alert.setContentText("Veuillez sélectionner une compagnie dans la liste pour afficher les détails.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleShowAirlineDetails(compagnie_aerienne airline) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/showairline.fxml"));
            Parent root = loader.load();

            showairline controller = loader.getController();
            controller.setAirline(airline);

            Stage stage = new Stage();
            stage.setTitle("Détails de la compagnie");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}