package project.controllers;

import com.google.zxing.WriterException;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import project.models.Reservation;
import project.service.PdfGenerateurService;
import project.service.QRCodeService; // Importez le service QRCode
import project.service.Reservationservice;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class ListReservationController {

    @FXML
    private TableView<Reservation> reservationsTableView;

    @FXML
    private TableColumn<Reservation, Integer> idColumn;

    @FXML
    private TableColumn<Reservation, String> userNameColumn;

    @FXML
    private TableColumn<Reservation, String> emailColumn;

    @FXML
    private TableColumn<Reservation, String> contactColumn;

    @FXML
    private TableColumn<Reservation, String> departColumn;

    @FXML
    private TableColumn<Reservation, String> destinationColumn;

    @FXML
    private TableColumn<Reservation, Float> priceColumn;

    @FXML
    private TableColumn<Reservation, String> agenceColumn;

    @FXML
    private TextField searchInput;

    @FXML
    private Button sortNewest;

    @FXML
    private Button sortOldest;

    @FXML
    private TableColumn<Reservation, Void> actionColumn;

    private Reservationservice reservationService;
    private PdfGenerateurService pdfGenerateurService = new PdfGenerateurService();
    private QRCodeService qrCodeService = new QRCodeService(); // Instance du service QRCode
    private ObservableList<Reservation> originalReservationList;
    private FilteredList<Reservation> filteredReservationList;
    private Predicate<Reservation> currentFilter;

    public void initialize() {
        reservationService = new Reservationservice();
        loadReservations();
        setupTableColumns();
        setupSearchFunctionality();
        currentFilter = reservation -> true;
    }

    private void loadReservations() {
        List<Reservation> reservations = reservationService.getAll();
        originalReservationList = FXCollections.observableArrayList(reservations);
        filteredReservationList = new FilteredList<>(originalReservationList, currentFilter);
        reservationsTableView.setItems(filteredReservationList);
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        departColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getRehla() != null) {
                return new SimpleStringProperty(cellData.getValue().getRehla().getDepart());
            }
            return new SimpleStringProperty("");
        });
        destinationColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getRehla() != null) {
                return new SimpleStringProperty(cellData.getValue().getRehla().getDestination());
            }
            return new SimpleStringProperty("");
        });
        priceColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getRehla() != null) {
                return new SimpleFloatProperty(cellData.getValue().getRehla().getPrice()).asObject();
            }
            return new SimpleFloatProperty(0.0f).asObject();
        });
        agenceColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getRehla() != null && cellData.getValue().getRehla().getAgence() != null) {
                return new SimpleStringProperty(cellData.getValue().getRehla().getAgence().getNom());
            }
            return new SimpleStringProperty("");
        });

        // Configuration de la colonne d'action pour la suppression, le QR Code et le PDF
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            private final Button qrCodeButton = new Button("QR Code");
            private final Button pdfButton = new Button("PDF");
            private final HBox actionButtons = new HBox(5);

            {
                deleteButton.setOnAction(event -> {
                    Reservation reservation = getTableRow().getItem();
                    if (reservation != null) {
                        handleDeleteAction(reservation);
                    }
                });

                qrCodeButton.setOnAction(event -> {
                    Reservation reservation = getTableRow().getItem();
                    if (reservation != null) {
                        generateQRCode(reservation);
                    }
                });

                pdfButton.setOnAction(event -> {
                    Reservation reservation = getTableRow().getItem();
                    if (reservation != null) {
                        pdfGenerateurService.generatePdfTicket(reservation);
                    }
                });

                actionButtons.getChildren().addAll(deleteButton, qrCodeButton, pdfButton);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionButtons);
                }
            }
        });
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

    private void handleDeleteAction(Reservation reservation) {
        reservationService.delete(reservation.getId());
        originalReservationList.remove(reservation);
        filteredReservationList.remove(reservation);
    }

    private void generateQRCode(Reservation reservation) {
        ImageView qrImageView = qrCodeService.generateReservationQRCode(reservation);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("QR Code");
        alert.setHeaderText("Reservation Details");
        alert.setGraphic(qrImageView);
        alert.showAndWait();
    }
}