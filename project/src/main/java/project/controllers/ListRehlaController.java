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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import project.models.aminemodels.compagnie_aerienne;
import project.models.aminemodels.rehla;
import project.service.amineservice.rehlaservice;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

public class ListRehlaController {

    @FXML
    private TextField searchField;
    @FXML
    private TextField departInput;
    @FXML
    private TextField searchInput; // Pour la destination
    @FXML
    private Slider minPriceRange;
    @FXML
    private Slider maxPriceRange;
    @FXML
    private DatePicker departDateInput;
    @FXML
    private DatePicker arrivalDateInput;
    @FXML
    private VBox agenceFilterVBox;
    @FXML
    private Label minPriceLabel;
    @FXML
    private Label maxPriceLabel;

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

        // Ajouter des listeners pour les champs de recherche départ et destination
        departInput.textProperty().addListener((obs, oldVal, newVal) -> {
            applyFilters();
        });

        searchInput.textProperty().addListener((obs, oldVal, newVal) -> {
            applyFilters();
        });

        // Initialize listeners for price sliders
        minPriceRange.valueProperty().addListener((obs, oldVal, newVal) -> {
            minPriceLabel.setText(String.format("%.0f €", newVal.doubleValue()));
            applyFilters();
        });

        maxPriceRange.valueProperty().addListener((obs, oldVal, newVal) -> {
            maxPriceLabel.setText(String.format("%.0f €", newVal.doubleValue()));
            applyFilters();
        });

        // Add listeners for date pickers
        departDateInput.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        arrivalDateInput.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        // Populate the agency filter
        populateAgenceFilter();
    }

    private void populateAgenceFilter() {
        // Clear existing checkboxes to avoid duplicates
        agenceFilterVBox.getChildren().clear();

        // Use a Set to ensure unique agencies
        Set<compagnie_aerienne> uniqueAgences = new HashSet<>();

        for (rehla vol : service.getAll()) {
            uniqueAgences.add(vol.getAgence());
        }

        for (compagnie_aerienne agence : uniqueAgences) {
            CheckBox checkBox = new CheckBox(agence.getNom());
            checkBox.setOnAction(event -> applyFilters());
            agenceFilterVBox.getChildren().add(checkBox);
        }
    }

    private void applyFilters() {
        String departKeyword = departInput.getText().toLowerCase();
        String destinationKeyword = searchInput.getText().toLowerCase();
        double minPrice = minPriceRange.getValue();
        double maxPrice = maxPriceRange.getValue();
        LocalDateTime selectedDepartDate = departDateInput != null ? departDateInput.getValue() != null ? departDateInput.getValue().atStartOfDay() : null : null;
        LocalDateTime selectedArrivalDate = arrivalDateInput != null ? arrivalDateInput.getValue() != null ? arrivalDateInput.getValue().atStartOfDay() : null : null;
        List<String> selectedAgences = agenceFilterVBox.getChildren().stream()
                .filter(node -> node instanceof CheckBox && ((CheckBox) node).isSelected())
                .map(node -> ((CheckBox) node).getText())
                .collect(Collectors.toList());

        List<rehla> filtered = rehlaList.stream()
                .filter(rehla ->
                        (departKeyword.isEmpty() || rehla.getDepart().toLowerCase().contains(departKeyword)) &&
                                (destinationKeyword.isEmpty() || rehla.getDestination().toLowerCase().contains(destinationKeyword)) &&
                                rehla.getPrice() >= minPrice &&
                                rehla.getPrice() <= maxPrice &&
                                (selectedDepartDate == null || (rehla.getDepart_date() != null && rehla.getDepart_date().toLocalDate().equals(selectedDepartDate.toLocalDate()))) &&
                                (selectedArrivalDate == null || (rehla.getArrival_date() != null && rehla.getArrival_date().toLocalDate().equals(selectedArrivalDate.toLocalDate()))) &&
                                (selectedAgences.isEmpty() || selectedAgences.contains(rehla.getAgence().getNom()))
                )
                .collect(Collectors.toList());
        rehlaTable.setItems(FXCollections.observableArrayList(filtered));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/amineviews/AddRehlaForm.fxml"));
            Parent root = loader.load();
            AddRehlaFormController controller = loader.getController();
            controller.setListRehlaController(this);
            Stage stage = new Stage();
            stage.setTitle("Ajouter un vol");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadRehlas();
            populateAgenceFilter(); // Rafraîchir les filtres après ajout
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateRehla(rehla rehlaToUpdate) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/amineviews/UpdateRehlaForm.fxml"));
            Parent root = loader.load();
            updaterehlacontroller controller = loader.getController();
            controller.setRehlaToEdit(rehlaToUpdate);
            controller.setListRehlaController(this);
            Stage stage = new Stage();
            stage.setTitle("Modifier le Vol");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadRehlas();
            populateAgenceFilter(); // Rafraîchir les filtres après modification
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteSelectedRehla(rehla rehlaToDelete) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/amineviews/DeleteRehlaForm.fxml"));
            Parent root = loader.load();
            deleterehlacontroller controller = loader.getController();
            controller.setRehlaToDelete(rehlaToDelete);
            controller.setListRehlaController(this);
            Stage stage = new Stage();
            stage.setTitle("Supprimer le Vol");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadRehlas();
            populateAgenceFilter(); // Rafraîchir les filtres après suppression
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleShowSelectedRehlaDetails(rehla rehlaDetails) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/amineviews/ShowRehlaDetails.fxml"));
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

    @FXML
    private void resetFilters(ActionEvent event) {
        if (searchInput != null) searchInput.clear();
        if (departInput != null) departInput.clear();
        if (minPriceRange != null) minPriceRange.setValue(0);
        if (maxPriceRange != null) maxPriceRange.setValue(2000);
        if (departDateInput != null) departDateInput.setValue(null);
        if (arrivalDateInput != null) arrivalDateInput.setValue(null);
        if (agenceFilterVBox != null) {
            agenceFilterVBox.getChildren().forEach(node -> {
                if (node instanceof CheckBox) {
                    ((CheckBox) node).setSelected(false);
                }
            });
        }
        applyFilters(); // Re-apply filters after reset
    }
}