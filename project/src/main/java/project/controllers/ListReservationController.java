package project.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import project.models.Reservation;
import project.service.Reservationservice;

import java.util.List; // Import the List interface

public class ListReservationController {

    @FXML
    private TableView<Reservation> reservationsTableView;

    @FXML
    private TableColumn<Reservation, Integer> idColumn;

    @FXML
    private TableColumn<Reservation, Integer> rehlaIdColumn;

    @FXML
    private TableColumn<Reservation, String> userNameColumn;

    @FXML
    private TableColumn<Reservation, String> emailColumn;

    @FXML
    private TableColumn<Reservation, String> contactColumn;

    @FXML
    private TableColumn<Reservation, Integer> userIdColumn;

    private Reservationservice reservationService;
    private ObservableList<Reservation> reservationList;

    public void initialize() {
        reservationService = new Reservationservice();
        loadReservations();
        setupTableColumns();
    }

    private void loadReservations() {
        List<Reservation> reservations = reservationService.getAll();
        reservationList = FXCollections.observableArrayList(reservations);
        reservationsTableView.setItems(reservationList);
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        rehlaIdColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getRehla() != null) {
                return new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getRehla().getId()).asObject();
            }
            return new javafx.beans.property.SimpleIntegerProperty(0).asObject(); // Or handle null as needed
        });
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
    }
}