package project.controllers;

import javafx.event.ActionEvent;
import javafx.scene.layout.HBox;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import project.models.compagnie_aerienne;
import project.models.rehla;
import project.service.rehlaservice;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ListRehlaController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<rehla> rehlaTable;

    @FXML
    private TableColumn<rehla, Integer> idColumn;

    @FXML
    private TableColumn<rehla, compagnie_aerienne> agenceColumn;

    @FXML
    private TableColumn<rehla, String> departColumn;

    @FXML
    private TableColumn<rehla, String> destinationColumn;

    @FXML
    private TableColumn<rehla, LocalDateTime> departDateColumn;

    @FXML
    private TableColumn<rehla, LocalDateTime> arrivalDateColumn;

    @FXML
    private TableColumn<rehla, Float> priceColumn;

    @FXML
    private TableColumn<rehla, Void> actionColumn;

    private final rehlaservice service = new rehlaservice();
    private final ObservableList<rehla> rehlaList = FXCollections.observableArrayList();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        agenceColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getAgence()));
        agenceColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(compagnie_aerienne item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNom());
                }
            }
        });
        departColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDepart()));
        destinationColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDestination()));
        departDateColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getDepart_date()));
        arrivalDateColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getArrival_date()));
        priceColumn.setCellValueFactory(data -> new SimpleFloatProperty(data.getValue().getPrice()).asObject());

        departDateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item.format(dateFormatter));
                }
            }
        });

        arrivalDateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item.format(dateFormatter));
                }
            }
        });

        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Supprimer");
            private final Button updateButton = new Button("Modifier");
            private final Button detailsButton = new Button("Détails");
            private final HBox buttonBox = new HBox(deleteButton, updateButton, detailsButton);

            {
                buttonBox.setSpacing(5);
                deleteButton.setOnAction(event -> {
                    rehla flight = getTableRow().getItem();
                    if (flight != null) {
                        handleDeleteSelectedRehla(flight);
                    }
                });
                updateButton.setOnAction(event -> {
                    rehla flight = getTableRow().getItem();
                    if (flight != null) {
                        handleUpdateRehla(flight);
                    }
                });
                detailsButton.setOnAction(event -> {
                    rehla flight = getTableRow().getItem();
                    if (flight != null) {
                        handleShowSelectedRehlaDetails(flight);
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

        loadRehlas();

        searchField.setOnKeyReleased(this::handleSearch);
    }

    void loadRehlas() {
        List<rehla> flights = service.getAll();
        rehlaList.setAll(flights);
        rehlaTable.setItems(rehlaList);
    }

    private void handleSearch(KeyEvent event) {
        String keyword = searchField.getText().toLowerCase();
        List<rehla> filtered = rehlaList.stream()
                .filter(f ->
                        String.valueOf(f.getId()).contains(keyword) ||
                                f.getAgence().getNom().toLowerCase().contains(keyword) ||
                                f.getDepart().toLowerCase().contains(keyword) ||
                                f.getDestination().toLowerCase().contains(keyword) ||
                                f.getDepart_date().format(dateFormatter).contains(keyword) ||
                                f.getArrival_date().format(dateFormatter).contains(keyword) ||
                                String.valueOf(f.getPrice()).contains(keyword)
                )
                .collect(Collectors.toList());
        rehlaTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void handleAddRehla() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddRehlaForm.fxml"));
            Parent root = loader.load();
            AddRehlaFormController controller = loader.getController();
            controller.setListRehlaController(this);
            Stage stage = new Stage();
            stage.setTitle("Ajouter un vol");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadRehlas();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateRehla(rehla rehlaToUpdate) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UpdateRehlaForm.fxml"));
            Parent root = loader.load();
            updaterehlacontroller controller = loader.getController();
            controller.setRehlaToEdit(rehlaToUpdate);
            controller.setListRehlaController(this);
            Stage stage = new Stage();
            stage.setTitle("Modifier le Vol");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadRehlas();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteSelectedRehla(rehla rehlaToDelete) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/DeleteRehlaForm.fxml"));
            Parent root = loader.load();
            deleterehlacontroller controller = loader.getController();
            controller.setRehlaToDelete(rehlaToDelete);
            controller.setListRehlaController(this);
            Stage stage = new Stage();
            stage.setTitle("Supprimer le Vol");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadRehlas();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleShowSelectedRehlaDetails(rehla rehlaDetails) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ShowRehlaDetails.fxml"));
            Parent root = loader.load();
            showrehlacontroller controller = loader.getController();
            controller.setRehla(rehlaDetails);
            Stage stage = new Stage();
            stage.setTitle("Détails du Vol");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateSelectedRehla() {
        rehla selectedRehla = rehlaTable.getSelectionModel().getSelectedItem();
        if (selectedRehla != null) {
            handleUpdateRehla(selectedRehla);
        } else {
            showAlert("Veuillez sélectionner un vol à modifier.");
        }
    }

    @FXML
    private void handleDeleteSelectedRehlaFromButton() {
        rehla selectedRehla = rehlaTable.getSelectionModel().getSelectedItem();
        if (selectedRehla != null) {
            handleDeleteSelectedRehla(selectedRehla);
        } else {
            showAlert("Veuillez sélectionner un vol à supprimer.");
        }
    }

    @FXML
    private void handleShowSelectedRehlaDetailsFromButton() {
        rehla selectedRehla = rehlaTable.getSelectionModel().getSelectedItem();
        if (selectedRehla != null) {
            handleShowSelectedRehlaDetails(selectedRehla);
        } else {
            showAlert("Veuillez sélectionner un vol pour afficher les détails.");
        }
    }

    @FXML
    public void handleDeleteSelectedRehla(ActionEvent actionEvent) {
        rehla selectedRehla = rehlaTable.getSelectionModel().getSelectedItem();
        if (selectedRehla != null) {
            handleDeleteSelectedRehla(selectedRehla);
        } else {
            showAlert("Veuillez sélectionner un vol à supprimer.");
        }
    }

    @FXML
    public void handleUpdateRehla(ActionEvent actionEvent) {
        rehla selectedRehla = rehlaTable.getSelectionModel().getSelectedItem();
        if (selectedRehla != null) {
            handleUpdateRehla(selectedRehla);
        } else {
            showAlert("Veuillez sélectionner un vol à modifier.");
        }
    }

    @FXML
    public void handleShowSelectedRehlaDetails(ActionEvent actionEvent) {
        rehla selectedRehla = rehlaTable.getSelectionModel().getSelectedItem();
        if (selectedRehla != null) {
            handleShowSelectedRehlaDetails(selectedRehla);
        } else {
            showAlert("Veuillez sélectionner un vol pour afficher les détails.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Sélectionnez un vol");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}