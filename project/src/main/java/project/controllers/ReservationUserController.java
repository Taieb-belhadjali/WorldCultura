package project.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import project.models.Reservation;
import project.service.Reservationservice;
import project.service.StripeService;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ReservationUserController {

    @FXML
    private ListView<Reservation> userReservationsListView;

    @FXML
    private TextField searchInput;

    private Reservationservice reservationService;
    private StripeService stripeService;
    private static final String STATIC_USER_NAME = "Utilisateur Statique";
    private Reservation currentReservationToPay;
    private ObservableList<Reservation> originalReservationList;
    private FilteredList<Reservation> filteredReservationList;
    private Predicate<Reservation> currentFilter;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void initialize() {
        reservationService = new Reservationservice();
        stripeService = new StripeService();
        loadUserReservations();
        setupSearchFunctionality();
        setupListViewCellFactory();
    }

    private void loadUserReservations() {
        List<Reservation> allReservations = reservationService.getAll();
        List<Reservation> userReservationsList = allReservations.stream()
                .filter(reservation -> STATIC_USER_NAME.equals(reservation.getUserName()))
                .collect(Collectors.toList());
        originalReservationList = FXCollections.observableArrayList(userReservationsList);
        filteredReservationList = new FilteredList<>(originalReservationList, p -> true);
        userReservationsListView.setItems(filteredReservationList);
    }

    private void setupListViewCellFactory() {
        userReservationsListView.setCellFactory(param -> new ListCell<>() {
            private VBox card;
            private Label routeLabel;
            private Label datesLabel;
            private Label agencyLabel;
            private Label priceLabel;
            private HBox actionsBox;
            private Button deleteButton;
            private Button payButton;

            {
                card = new VBox(5);
                card.setPadding(new Insets(10));
                card.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-color: #f9f9f9; -fx-background-radius: 5;");
                card.setMaxWidth(Double.MAX_VALUE);

                routeLabel = new Label();
                routeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

                datesLabel = new Label();
                datesLabel.setStyle("-fx-text-fill: #777;");

                agencyLabel = new Label();
                agencyLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #555;");

                priceLabel = new Label();
                priceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

                deleteButton = new Button("Supprimer");
                deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 8 15; -fx-border-radius: 5; -fx-background-radius: 5;");
                deleteButton.setOnAction(event -> {
                    Reservation reservation = getItem();
                    if (reservation != null) {
                        handleDeleteAction(reservation);
                    }
                });

                payButton = new Button("Payer");
                payButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-padding: 8 15; -fx-border-radius: 5; -fx-background-radius: 5;");
                payButton.setOnAction(event -> {
                    Reservation reservation = getItem();
                    if (reservation != null) {
                        currentReservationToPay = reservation;
                        System.out.println("Payer button clicked for reservation ID: " + reservation.getId());
                        openPaymentWindow(reservation);
                    }
                });

                actionsBox = new HBox(10);
                actionsBox.getChildren().addAll(deleteButton, payButton);
                actionsBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                HBox priceAndActions = new HBox(10);
                priceAndActions.getChildren().addAll(priceLabel, new Pane(), actionsBox);
                HBox.setHgrow(new Pane(), javafx.scene.layout.Priority.ALWAYS);
                priceAndActions.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                card.getChildren().addAll(routeLabel, datesLabel, agencyLabel, priceAndActions);
                card.setMaxWidth(Double.MAX_VALUE);
            }

            @Override
            protected void updateItem(Reservation reservation, boolean empty) {
                super.updateItem(reservation, empty);
                if (empty || reservation == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String depart = (reservation.getRehla() != null && reservation.getRehla().getDepart() != null) ? reservation.getRehla().getDepart() : "N/A";
                    String destination = (reservation.getRehla() != null && reservation.getRehla().getDestination() != null) ? reservation.getRehla().getDestination() : "N/A";
                    double price = (reservation.getRehla() != null) ? reservation.getRehla().getPrice() : 0.0;
                    String agence = (reservation.getRehla() != null && reservation.getRehla().getAgence() != null) ? reservation.getRehla().getAgence().getNom() : "N/A";
                    String departDate = (reservation.getRehla() != null && reservation.getRehla().getDepart_date() != null) ? dateFormatter.format(reservation.getRehla().getDepart_date()) : "N/A";
                    String arrivalDate = (reservation.getRehla() != null && reservation.getRehla().getArrival_date() != null) ? dateFormatter.format(reservation.getRehla().getArrival_date()) : "N/A";

                    routeLabel.setText(depart + " → " + destination);
                    datesLabel.setText(departDate + "  " + arrivalDate);
                    agencyLabel.setText("Agence : " + agence);
                    priceLabel.setText(String.format("%.0f €", price));

                    setGraphic(card);
                }
            }
        });
    }

    private void handleDeleteAction(Reservation reservation) {
        reservationService.delete(reservation.getId());
        loadUserReservations();
    }

    private void openPaymentWindow(Reservation reservation) {
        System.out.println("Entering openPaymentWindow for reservation ID: " + reservation.getId());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/PaymentView.fxml"));
            VBox paymentRoot = loader.load();

            PaymentController paymentController = loader.getController();
            System.out.println("Payment Controller: " + paymentController);
            if (paymentController != null) {
                paymentController.loadPaymentView(reservation, stripeService);
            } else {
                System.err.println("Payment Controller is null!");
            }

            Scene scene = new Scene(paymentRoot);
            Stage paymentStage = new Stage();
            paymentStage.setTitle("Paiement sécurisé");
            paymentStage.initModality(Modality.APPLICATION_MODAL);
            paymentStage.setScene(scene);
            paymentStage.showAndWait();

            loadUserReservations();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible d'ouvrir la fenêtre de paiement.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void setupSearchFunctionality() {
        searchInput.textProperty().addListener((observable, oldValue, newValue) -> {
            currentFilter = reservation -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (String.valueOf(reservation.getId()).contains(lowerCaseFilter)) return true;
                if (reservation.getUserName().toLowerCase().contains(lowerCaseFilter)) return true;
                if (reservation.getEmail().toLowerCase().contains(lowerCaseFilter)) return true;
                if (reservation.getContact().toLowerCase().contains(lowerCaseFilter)) return true;
                if (reservation.getRehla() != null) {
                    if (reservation.getRehla().getDepart() != null && reservation.getRehla().getDepart().toLowerCase().contains(lowerCaseFilter)) return true;
                    if (reservation.getRehla().getDestination() != null && reservation.getRehla().getDestination().toLowerCase().contains(lowerCaseFilter)) return true;
                    if (String.valueOf(reservation.getRehla().getPrice()).contains(lowerCaseFilter)) return true;
                    if (reservation.getRehla().getAgence() != null && reservation.getRehla().getAgence().getNom() != null && reservation.getRehla().getAgence().getNom().toLowerCase().contains(lowerCaseFilter)) return true;
                }
                return false;
            };
            filteredReservationList.setPredicate(currentFilter);
        });
    }

    @FXML
    private void sortReservationsByNewest(ActionEvent event) {
        Comparator<Reservation> newestFirstComparator = Comparator.comparingInt(Reservation::getId).reversed();
        originalReservationList.sort(newestFirstComparator);
        filteredReservationList.setPredicate(currentFilter);
    }

    @FXML
    private void sortReservationsByOldest(ActionEvent event) {
        Comparator<Reservation> oldestFirstComparator = Comparator.comparingInt(Reservation::getId);
        originalReservationList.sort(oldestFirstComparator);
        filteredReservationList.setPredicate(currentFilter);
    }

    // Méthode à appeler après qu'une réservation a été effectuée
    public void showReservationConfirmation() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Votre réservation a été effectuée avec succès !");
        alert.showAndWait();
        loadUserReservations(); // Recharger la liste pour afficher la nouvelle réservation
    }

    @FXML
    private void switchToRehlaFront(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/rehla_front.fxml"));
            javafx.scene.Parent rehlaFrontRoot = loader.load();
            Scene scene = new Scene(rehlaFrontRoot);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Offres de Voyages");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de navigation");
            alert.setHeaderText("Impossible de charger la page des offres.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public static class Pane extends javafx.scene.layout.Pane {
        public Pane() {
            // Constructeur vide
        }
    }
}