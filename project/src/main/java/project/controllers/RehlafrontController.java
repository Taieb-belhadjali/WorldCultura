// project.controllers.RehlafrontController

package project.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import project.models.Reservation;
import project.models.compagnie_aerienne;
import project.models.rehla;
import project.service.WeatherService; // Import du nouveau service
import project.service.compagnie_areienneservice;
import project.service.rehlaservice;
import project.service.Reservationservice;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class RehlafrontController implements Initializable {

    @FXML
    private GridPane rehlaGridPane;

    @FXML
    private TextField searchInput;

    @FXML
    private TextField departInput;

    @FXML
    private Slider minPriceRange;

    @FXML
    private Label minPriceLabel;

    @FXML
    private Slider maxPriceRange;

    @FXML
    private Label maxPriceLabel;

    @FXML
    private DatePicker departDateInput;

    @FXML
    private DatePicker arrivalDateInput;

    @FXML
    private VBox agenceFilterVBox;

    @FXML
    private Button prevPageButton;

    @FXML
    private Label currentPageLabel;

    @FXML
    private Button nextPageButton;

    private final rehlaservice rehlaService = new rehlaservice();
    private final Reservationservice reservationService = new Reservationservice();
    private final compagnie_areienneservice compagnieAerienneService = new compagnie_areienneservice();
    private final WeatherService weatherService = new WeatherService(); // Instance du WeatherService
    private List<rehla> allRehlaList;
    private List<rehla> filteredRehlaList;
    private int currentPage = 1;
    private int rehlasPerPage = 8;
    private rehla selectedRehla;

    private static final String STATIC_USER_NAME = "Utilisateur Statique";
    private static final String STATIC_EMAIL = "static.user@example.com";
    private static final String STATIC_CONTACT = "0123456789";
    private static final Integer STATIC_USER_ID = 1;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        allRehlaList = rehlaService.getAll();
        filteredRehlaList = allRehlaList;
        loadRehlaFilters();
        updateRehlaGrid();
        updatePaginationControls();

        searchInput.textProperty().addListener((observable, oldValue, newValue) -> filterRehlas());
        departInput.textProperty().addListener((observable, oldValue, newValue) -> filterRehlas());
        minPriceRange.valueProperty().addListener((observable, oldValue, newValue) -> {
            minPriceLabel.setText(String.format("%.0f €", newValue.doubleValue()));
            filterRehlas();
        });
        maxPriceRange.valueProperty().addListener((observable, oldValue, newValue) -> {
            maxPriceLabel.setText(String.format("%.0f €", newValue.doubleValue()));
            filterRehlas();
        });
        if (departDateInput != null) {
            departDateInput.valueProperty().addListener((observable, oldValue, newValue) -> filterRehlas());
        }
        if (arrivalDateInput != null) {
            arrivalDateInput.valueProperty().addListener((observable, oldValue, newValue) -> filterRehlas());
        }
    }

    private void loadRehlaFilters() {
        List<compagnie_aerienne> agences = compagnieAerienneService.getAll();
        if (agenceFilterVBox != null) {
            for (compagnie_aerienne agence : agences) {
                CheckBox checkBox = new CheckBox(agence.getNom());
                checkBox.setOnAction(event -> filterRehlas());
                agenceFilterVBox.getChildren().add(checkBox);
            }
        }
    }

    private void updateRehlaGrid() {
        rehlaGridPane.getChildren().clear();
        int startIndex = (currentPage - 1) * rehlasPerPage;
        int endIndex = Math.min(startIndex + rehlasPerPage, filteredRehlaList.size());

        int row = 0;
        int col = 0;

        for (int i = startIndex; i < endIndex; i++) {
            rehla vol = filteredRehlaList.get(i);
            VBox rehlaCard = createRehlaCard(vol);
            rehlaGridPane.add(rehlaCard, col, row);

            col++;
            if (col > 1) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createRehlaCard(rehla vol) {
        VBox card = new VBox(5);
        card.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-padding: 15; -fx-background-color: #ffffff; -fx-border-radius: 5; -fx-background-radius: 5;");
        card.setPrefWidth(280);

        Label departDestination = new Label(vol.getDepart() + " → " + vol.getDestination());
        departDestination.setFont(Font.font("Arial", 16));

        HBox datesBox = new HBox(5);
        datesBox.setAlignment(Pos.CENTER_LEFT);
        Label departDateLabel = new Label(vol.getDepart_date() != null ? dateFormatter.format(vol.getDepart_date()) : "-");
        Label arrivalDateLabel = new Label(" ⏳ " + (vol.getArrival_date() != null ? dateFormatter.format(vol.getArrival_date()) : "-"));
        datesBox.getChildren().addAll(departDateLabel, arrivalDateLabel);

        Label agencyLabel = new Label("Agence : " + (vol.getAgence() != null ? vol.getAgence().getNom() : "Non spécifié"));
        Label priceLabel = new Label("Prix : " + vol.getPrice() + " €");
        priceLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        HBox priceReserveBox = new HBox(10);
        priceReserveBox.setAlignment(Pos.CENTER_RIGHT);
        Label priceValueLabel = new Label(vol.getPrice() + " €");
        priceValueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        priceValueLabel.setStyle("-fx-text-fill: #007bff;");

        Button reserverButton = new Button("Réserver");
        reserverButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5;");
        reserverButton.setOnAction(event -> handleReserverButtonClick(vol)); // Correction ici

        priceReserveBox.getChildren().addAll(priceValueLabel, reserverButton);

        Label weatherLabel = new Label("Chargement de la météo...");
        weatherLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #6c757d;");

        if (vol.getDestination() != null && !vol.getDestination().isEmpty()) {
            weatherService.getWeatherForCity(vol.getDestination())
                    .thenAccept(weatherInfo -> {
                        if (weatherInfo != null) {
                            weatherLabel.setText(weatherInfo);
                        } else {
                            weatherLabel.setText("Météo non disponible pour " + vol.getDestination());
                        }
                    })
                    .exceptionally(e -> {
                        weatherLabel.setText("Erreur lors de la récupération de la météo");
                        e.printStackTrace();
                        return null;
                    });
        }

        card.getChildren().addAll(departDestination, datesBox, agencyLabel, priceLabel, weatherLabel, priceReserveBox);
        return card;
    }

    private void filterRehlas() {
        filteredRehlaList = allRehlaList.stream().filter(vol -> {
            boolean destinationMatch = vol.getDestination().toLowerCase().contains(searchInput.getText().toLowerCase());
            boolean departMatch = vol.getDepart().toLowerCase().contains(departInput.getText().toLowerCase());
            boolean priceMatch = vol.getPrice() >= minPriceRange.getValue() && vol.getPrice() <= maxPriceRange.getValue();
            boolean dateDepartMatch = (departDateInput.getValue() == null) || (vol.getDepart_date() != null && vol.getDepart_date().toLocalDate().equals(departDateInput.getValue()));
            boolean dateArrivalMatch = (arrivalDateInput.getValue() == null) || (vol.getArrival_date() != null && vol.getArrival_date().toLocalDate().equals(arrivalDateInput.getValue()));
            boolean agenceMatch = true;
            if (agenceFilterVBox != null) {
                List<String> selectedAgences = agenceFilterVBox.getChildren().stream()
                        .filter(node -> node instanceof CheckBox && ((CheckBox) node).isSelected())
                        .map(node -> ((CheckBox) node).getText())
                        .collect(Collectors.toList());
                if (!selectedAgences.isEmpty()) {
                    agenceMatch = (vol.getAgence() != null && selectedAgences.contains(vol.getAgence().getNom()));
                }
            }
            return destinationMatch && departMatch && priceMatch && dateDepartMatch && dateArrivalMatch && agenceMatch;
        }).collect(Collectors.toList());
        currentPage = 1;
        updateRehlaGrid();
        updatePaginationControls(); // Appel corrigé
    }

    @FXML
    private void resetFilters(ActionEvent event) {
        searchInput.clear();
        departInput.clear();
        minPriceRange.setValue(0);
        maxPriceRange.setValue(2000);
        if (departDateInput != null) departDateInput.setValue(null);
        if (arrivalDateInput != null) arrivalDateInput.setValue(null);
        if (agenceFilterVBox != null) {
            agenceFilterVBox.getChildren().forEach(node -> {
                if (node instanceof CheckBox) {
                    ((CheckBox) node).setSelected(false);
                }
            });
        }
        filterRehlas();
    }

    @FXML
    private void prevPage(ActionEvent event) {
        if (currentPage > 1) {
            currentPage--;
            updateRehlaGrid();
            updatePaginationControls();
        }
    }

    @FXML
    private void nextPage(ActionEvent event) {
        int totalPages = (int) Math.ceil((double) filteredRehlaList.size() / rehlasPerPage);
        if (currentPage < totalPages) {
            currentPage++;
            updateRehlaGrid();
            updatePaginationControls();
        }
    }

    private void updatePaginationControls() {
        int totalPages = (int) Math.ceil((double) filteredRehlaList.size() / rehlasPerPage);
        currentPageLabel.setText("Page " + currentPage + " / " + totalPages);
        prevPageButton.setDisable(currentPage == 1);
        nextPageButton.setDisable(currentPage == totalPages || totalPages == 0);
    }

    private void handleReserverButtonClick(rehla vol) {
        selectedRehla = vol;
        Reservation nouvelleReservation = new Reservation();
        nouvelleReservation.setRehla(selectedRehla);
        nouvelleReservation.setUserName(STATIC_USER_NAME);
        nouvelleReservation.setEmail(STATIC_EMAIL);
        nouvelleReservation.setContact(STATIC_CONTACT);
        nouvelleReservation.setUserId(STATIC_USER_ID);

        reservationService.add(nouvelleReservation);
        System.out.println("Réservation effectuée pour le vol ID: " + selectedRehla.getId() + " par l'utilisateur statique.");

        // Afficher l'alerte de confirmation
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Votre réservation pour " + vol.getDepart() + " → " + vol.getDestination() + " a été effectuée avec succès !");
            alert.showAndWait();
        });
    }

    @FXML
    private void consulterReservations(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/reservationuser.fxml"));
            Parent reservationUserRoot = loader.load();

            Scene scene = new Scene(reservationUserRoot);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // Gérer l'erreur de chargement de la vue
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erreur de navigation");
            errorAlert.setHeaderText("Impossible de charger la page des réservations.");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
        }
    }
}